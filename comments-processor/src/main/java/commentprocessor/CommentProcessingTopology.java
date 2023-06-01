package commentprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commentprocessor.model.*;
import commentprocessor.serialization.json.JsonSerdes;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.apache.kafka.streams.state.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

public class CommentProcessingTopology {
    private static final String LANGUAGE_DETECTION_URL = "http://localhost:2000/predict";
    private static final List<String> FILTERED_WORDS = Arrays.asList("in_shit", "Christ_on_a_cracker", "Christ_on_a_bike", "sweet_Jesus", "shit_ass", "damn_it", "Jesus_fuck", "godsdamn", "child-fucker", "arsehead", "dyke", "son_of_a_whore", "frigger", "fatherfucker", "brotherfucker", "Jesus_Mary_and_Joseph", "Jesus_wept", "arse", "arsehole", "ass", "asshole", "bastard", "bitch", "bloody", "bollocks", "bugger", "bullshit", "cock", "cocksucker", "crap", "cunt", "damn", "dick", "dickhead", "fatherfucker", "goddamn", "hell", "holy_shit", "horseshit", "Jesus_Christ", "Jesus_H_Christ", "Jesus_Harold_Christ", "kike", "motherfucker", "nigga", "nigra", "piss", "prick", "pussy", "shit", "shite", "sisterfucker", "slut", "son_of_a_bitch", "spastic", "turd", "twat", "wanker","EDPO");
    private static final Pattern EMOJI_PATTERN = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");

    private static Cache<Comment, String> cache = new LRUCache<>(1000);

    public static String languageStore;
    public static String lastCommentsStore;
    public static String commentCountStore;

    public static String windowedSentimentStore;

    public static String windowedCountStore;

    public static String totalSentimentStore;

    public static Topology build() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<byte[], CommentBatchEvent> commentBatchStream =
                builder
                        .stream("new-comment-batches", Consumed.with(Serdes.ByteArray(), JsonSerdes.CommentBatch()));

        commentBatchStream.foreach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
        // Define the state store.

        StoreBuilder<KeyValueStore<String, Boolean>> deduplicationStoreBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("deduplication-store"),
                        Serdes.String(),
                        JsonSerdes.Boolean()
                );

        builder.addStateStore(deduplicationStoreBuilder);


        KStream<String, Comment> comments = commentBatchStream.flatMapValues(batch -> batch.getComments()).selectKey((key, comment) -> comment.getCommentId());
        comments = removeEmojisFromComments(comments);

        comments = comments.transform(() -> new DeduplicationTransformer(), "deduplication-store");

        KStream<String, Comment> commentsWithLanguage = comments.transform(new RetryTransformerSupplier());

        Predicate<String, Comment> isEnglish = (key, comment) -> "en".equalsIgnoreCase(comment.getLanguage());

        KStream<String, Comment>[] branches = commentsWithLanguage.branch(isEnglish, (key, value) -> !isEnglish.test(key, value));

        KStream<String, Comment> english_stream = branches[0];
        KStream<String, Comment> non_english_stream = branches[1];

        KStream<String, Comment> translatable_comments_stream = non_english_stream.filter((key, comment) -> CommentTranslator.availableLanguages.contains(comment.getLanguage()));

        translatable_comments_stream.filter((s, comment) -> !CommentTranslator.availableLanguages.contains(comment.getLanguage())).to("untranslatable-comments");

        KStream<String, Comment> translated_stream = translatable_comments_stream.transform(() -> new CommentTranslator());

        KStream<String, Comment> merged_stream = english_stream.merge(translated_stream);

        KStream<String, Comment> filteredCommentStream = filterComments(merged_stream);

        KStream<String, Comment> sentiment_classified_stream = filteredCommentStream.transform(() -> new SentimentAnalyzer());

        //sentiment_classified_stream.to("comment-classified");

        sentiment_classified_stream.peek((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        KGroupedStream<String, Comment> groupedStream = sentiment_classified_stream.groupBy(
                (key, comment) -> comment.getVideoId(), Grouped.with(Serdes.String(), JsonSerdes.Comment()));

        Initializer<Languages> languagesInitializer = Languages::new;

        Aggregator<String, Comment, Languages> languagesAdder = (key, comment, languages) -> languages.add(comment);

        KTable<String, Languages> languagesKTable =
                groupedStream.aggregate(
                        languagesInitializer,
                        languagesAdder,
                        Materialized.<String, Languages, KeyValueStore<Bytes, byte[]>>
                                        as("comment-count-languages-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.Languages()));
        languageStore = languagesKTable.queryableStoreName();

        KTable<String, Long> comment_count = groupedStream.count(
                Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("comment-total-count-store")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long())
        );
        commentCountStore = comment_count.queryableStoreName();

        Initializer<RecentComments> recentCommentsInitializer = RecentComments::new;
        Aggregator<String, Comment, RecentComments> recentCommentsAdder = (key, comment, recentComments) -> recentComments.add(comment);
        KTable<String, RecentComments> recentCommentsKtable =
                groupedStream.aggregate(
                        recentCommentsInitializer,
                        recentCommentsAdder,
                        Materialized.<String, RecentComments, KeyValueStore<Bytes, byte[]>>
                                        as("last-5-comment-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.RecentComments()));

        lastCommentsStore = recentCommentsKtable.queryableStoreName();

        // Initializer for the sentiment score
        Initializer<Double> sentimentInitializer = () -> 0.0;

        // Aggregator for the sentiment score
        Aggregator<String, Comment, Double> sentimentAggregator = (key, comment, aggregate) -> aggregate + comment.getSentimentScore();

        KTable<String, Double> totalSentimentKTable = groupedStream.aggregate(sentimentInitializer, sentimentAggregator,
                                Materialized.<String, Double, KeyValueStore<Bytes, byte[]>>as("total-sentiment-store")
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(Serdes.Double()));

        totalSentimentStore = totalSentimentKTable.queryableStoreName();

        Duration windowSize = Duration.ofHours(24);
        Duration advanceInterval = Duration.ofMinutes(1); // This is how often the window should "slide". Adjust according to your needs.
        /**
        KTable<Windowed<String>, Long> windowedCommentCountKTable = groupedStream
                .windowedBy(TimeWindows.of(windowSize).grace(advanceInterval))
                .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("windowed-total-count-store")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long())
                );
         windowedCountStore = windowedCommentCountKTable.queryableStoreName();
         **/

        final Duration retentionPeriod = windowSize.plus(advanceInterval);

        KTable<Windowed<String>, Double> windowedSentiment =
                groupedStream
                        .windowedBy(TimeWindows.of(windowSize).grace(advanceInterval))
                        .aggregate(
                                // Initialize the aggregate value
                                () -> 0.0,
                                // Adder (aggregator function)
                                (k, newComment, aggSentiment) -> aggSentiment + newComment.getSentimentScore(),
                                // Materialize the state store
                                Materialized.<String, Double, WindowStore<Bytes, byte[]>>as("windowed-sentiment-store")
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(Serdes.Double())
                                        .withRetention(retentionPeriod)  // Set the retention period
                        );

        windowedSentimentStore = windowedSentiment.queryableStoreName();


        languagesKTable.toStream().peek((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
        //Counting the comments grouped by videoId and language

        System.out.println(languagesKTable.queryableStoreName());

        //sentiment_classified_stream.foreach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
        return builder.build();
    }

    public static String predictLanguage(Comment comment) throws JsonProcessingException, UnirestException {
        if (cache.get(comment) != null) {
            return cache.get(comment);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String commentText = comment.getComment();
        if (commentText.length() > 512)
            commentText = commentText.substring(0, 512);


        String jsonData = objectMapper.writeValueAsString(Collections.singletonMap("text",commentText));

        System.out.println("Calling language detection API " + jsonData);
        String result = Unirest.post(LANGUAGE_DETECTION_URL)
                .contentType("application/json")
                .body(jsonData)
                .asString()
                .getBody();


        JsonNode languageNode = objectMapper.readTree(result);
        String language = languageNode.get("language").asText();

        cache.put(comment, language);

        return language;

        }

    private static KStream<String, Comment> filterComments(KStream<String, Comment> comments) {
        return comments.filter((key, comment) -> {
            if (comment.getComment() == null || comment.getComment().trim().isEmpty()) {
                return false;
            }
            for (String word : FILTERED_WORDS) {
                if (comment.getComment().contains(word)) {
                    return false;
                }
            }
            return true;
        });
    }

    private static KStream<String, Comment> removeEmojisFromComments(KStream<String, Comment> comments) {
        return comments.mapValues(comment -> {
            String commentText = comment.getComment();
            // Remove any emojis from the comment
            String filteredComment = EMOJI_PATTERN.matcher(commentText).replaceAll("");
            comment.setComment(filteredComment);
            return comment;
        });
    }

    public static class CommentTimestampExtractor implements TimestampExtractor {

        public CommentTimestampExtractor() {
        }
        @Override
        public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
            if (record.value() instanceof CommentBatchEvent) {
                CommentBatchEvent commentBatchEvent = (CommentBatchEvent) record.value();
                return Instant.parse(commentBatchEvent.getTimestamp()).toEpochMilli();
            }
            else if (record.value() instanceof Comment) {
                Comment comment = (Comment) record.value();
                return Instant.parse(comment.getTimestamp()).toEpochMilli();
            }

            return System.currentTimeMillis();
        }
    }
}

