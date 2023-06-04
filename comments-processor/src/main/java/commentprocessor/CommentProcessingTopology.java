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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

public class CommentProcessingTopology {
    private static final String LANGUAGE_DETECTION_URL = "http://" + System.getProperty("language_detection.base", "localhost:2000") + "/predict";
    private static final List<String> FILTERED_WORDS = Arrays.asList("in_shit", "Christ_on_a_cracker", "Christ_on_a_bike", "sweet_Jesus", "shit_ass", "damn_it", "Jesus_fuck", "godsdamn", "child-fucker", "arsehead", "dyke", "son_of_a_whore", "frigger", "fatherfucker", "brotherfucker", "Jesus_Mary_and_Joseph", "Jesus_wept", "arse", "arsehole", "ass", "asshole", "bastard", "bitch", "bloody", "bollocks", "bugger", "bullshit", "cock", "cocksucker", "crap", "cunt", "damn", "dick", "dickhead", "fatherfucker", "goddamn", "hell", "holy_shit", "horseshit", "Jesus_Christ", "Jesus_H_Christ", "Jesus_Harold_Christ", "kike", "motherfucker", "nigga", "nigra", "piss", "prick", "pussy", "shit", "shite", "sisterfucker", "slut", "son_of_a_bitch", "spastic", "turd", "twat", "wanker","EDPO");
    private static final Pattern EMOJI_PATTERN = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");

    private static Cache<Comment, String> cache = new LRUCache<>(1000);

    public static String languageStore;
    public static String lastCommentsStore;
    public static String commentCountStore;

    public static String windowedSentimentStore;

    public static String windowedCountStore;

    public static String totalSentimentStore;

    private static final Logger logger = LoggerFactory.getLogger(CommentProcessingTopology.class);


    public static Topology build() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<byte[], CommentBatchEvent> commentBatchStream =
                builder
                        .stream("new-comment-batches", Consumed.with(Serdes.ByteArray(), JsonSerdes.CommentBatch()));

        commentBatchStream.foreach((key, value) -> logger.debug("Key: " + key + ", Value: " + value));
        // Define the state store.

        StoreBuilder<KeyValueStore<String, Boolean>> deduplicationStoreBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("deduplication-store"),
                        Serdes.String(),
                        JsonSerdes.Boolean()
                );

        builder.addStateStore(deduplicationStoreBuilder);

        //We have the reverse the List as we need it in reverse chronological order for the windowed aggregation later
        KStream<String, Comment> comments = commentBatchStream
                .mapValues(CommentBatchEvent::getComments)  // Convert CommentBatchEvent to List<Comment>
                .flatMapValues(commentBatch -> {
                    // Create a new list to avoid UnsupportedOperationException when calling reverse
                    List<Comment> commentsList = new ArrayList<>(commentBatch);
                    // Reverse the list
                    Collections.reverse(commentsList);
                    return commentsList;
                })
                .selectKey((key, comment) -> comment.getCommentId());

        comments = removeEmojisFromComments(comments);

        //Here we need to override the timestamp of every comment event with the one we get from youtube, so we are able to use the windows later on
        comments = comments.transform(OverrideCommentTimestampTransformer::new);

        //When a new comment is postet the scraper sends also some old comments in the batch, so we need to filter them out so they don't get processed again
        comments = comments.transform(DeduplicationTransformer::new, "deduplication-store");

        //Here we are using the language classifier to detect the language of the comment
        KStream<String, Comment> commentsWithLanguage = comments.transform(new TranslationTransformer());

        //Here we are filtering all comments that are not in english
        Predicate<String, Comment> isEnglish = (key, comment) -> "en".equalsIgnoreCase(comment.getLanguage());

        //Non english comments are branched into another stream
        KStream<String, Comment>[] branches = commentsWithLanguage.branch(isEnglish, (key, value) -> !isEnglish.test(key, value));

        KStream<String, Comment> english_stream = branches[0];
        KStream<String, Comment> non_english_stream = branches[1];

        //If the comment is in a language the translator cannot translate we filter them
        KStream<String, Comment> translatable_comments_stream = non_english_stream.filter((key, comment) -> CommentTranslator.availableLanguages.contains(comment.getLanguage()));

        //Untranslatable comments are send into a dead letter queue
        translatable_comments_stream.filter((s, comment) -> !CommentTranslator.availableLanguages.contains(comment.getLanguage())).to("untranslatable-comments");

        //Here we are translating the non english comments
        KStream<String, Comment> translated_stream = translatable_comments_stream.transform(CommentTranslator::new);

        //Here we are merging the english and translated comments back together
        KStream<String, Comment> merged_stream = english_stream.merge(translated_stream);

        //Here we are filtering all comments that contain blacklisted words
        KStream<String, Comment> filteredCommentStream = filterComments(merged_stream);

        //Here we are classifying the sentiment of the comments
        KStream<String, Comment> sentiment_classified_stream = filteredCommentStream.transform(SentimentAnalyzer::new);

        //sentiment_classified_stream.to("comment-classified");

        //Print the stream to the console, so we can se the progress
        sentiment_classified_stream.peek((key, value) -> logger.info("Key: " + key + ", Value: " + value));

        //Group the comments by video id
        KGroupedStream<String, Comment> groupedStream = sentiment_classified_stream.groupBy(
                (key, comment) -> comment.getVideoId(), Grouped.with(Serdes.String(), JsonSerdes.Comment()));

        Initializer<Languages> languagesInitializer = Languages::new;

        Aggregator<String, Comment, Languages> languagesAdder = (key, comment, languages) -> languages.add(comment);

        //Here we are using a custom adder to count the comments in each language
        KTable<String, Languages> languagesKTable =
                groupedStream.aggregate(
                        languagesInitializer,
                        languagesAdder,
                        Materialized.<String, Languages, KeyValueStore<Bytes, byte[]>>
                                        as("comment-count-languages-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.Languages()));
        languageStore = languagesKTable.queryableStoreName();

        //Here we are counting the comments for each video by just counting the events
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

        //Set the tumbling window to 1 day
        TimeWindows windows = TimeWindows.of(Duration.ofDays(1));
        //Retention period is set to 3000 days, so we can be sure to get enough data for the analysis
        Duration retention = Duration.ofDays(3000);

        //Here we are using the window to get the total sentiments by day
        KTable<Windowed<String>, Double> totalSentimentKTableWindowed = groupedStream
                .windowedBy(windows)
                .aggregate(sentimentInitializer, sentimentAggregator,
                        Materialized.<String, Double, WindowStore<Bytes, byte[]>>as("total-sentiment-store-windowed")
                                .withRetention(retention)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Double()));

        totalSentimentKTableWindowed.toStream().peek((key, value) -> logger.info("Key: " + key + ", Value: " + value));

        //Here we are using the window to get the total comments by day
        KTable<Windowed<String>, Long> commentCountWindowed = groupedStream
                .windowedBy(windows)
                .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("comment-total-count-store-windowed")
                        .withRetention(retention)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long()));


        //save the store names for the CommentService
        windowedSentimentStore = totalSentimentKTableWindowed.queryableStoreName();
        windowedCountStore = commentCountWindowed.queryableStoreName();

        //Print the stream to the console, so we can se the progress
        languagesKTable.toStream().peek((key, value) -> logger.info("Key: " + key + ", Value: " + value));
        //Counting the comments grouped by videoId and language

        logger.debug(languagesKTable.queryableStoreName());

        //sentiment_classified_stream.foreach((key, value) -> logger.debug("Key: " + key + ", Value: " + value));
        return builder.build();
    }

    public static String predictLanguage(Comment comment) throws JsonProcessingException, UnirestException {
        if (cache.get(comment) != null) {
            return cache.get(comment);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String commentText = comment.getComment();
        //The classifier can only handle 512 characters
        commentText = commentText.length() > 512 ? commentText.substring(0, 512): commentText;

        String jsonData = objectMapper.writeValueAsString(Collections.singletonMap("text",commentText));

        logger.debug("Calling language detection API " + jsonData);
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

