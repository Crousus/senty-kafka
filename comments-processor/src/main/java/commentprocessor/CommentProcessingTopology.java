package commentprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commentprocessor.model.*;
import commentprocessor.Languages;
import commentprocessor.serialization.json.JsonSerdes;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class CommentProcessingTopology {
    private static final String LANGUAGE_DETECTION_URL = "http://localhost:2000/predict";

    private static final String API_TOKEN = "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";

    private static final List<String> FILTERED_WORDS = Arrays.asList("spam", "ad", "junk");
    private static final Pattern EMOJI_PATTERN = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");

    private static Cache<Comment, String> cache = new LRUCache<>(1000);

    public static String languageStore;

    public static Topology build() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<byte[], CommentBatchEvent> commentBatchStream =
                builder
                        .stream("new-comment-batches", Consumed.with(Serdes.ByteArray(), JsonSerdes.CommentBatch()));

        commentBatchStream.foreach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
        // Define the state store.
        StoreBuilder<KeyValueStore<String, Comment>> retryBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("inmemory-order-create-retry"),
                        Serdes.String(),
                        JsonSerdes.Comment());  // Assuming you have a Serde for your Comment type.

        // Add the state store to the topology.
        builder.addStateStore(retryBuilder);

        KStream<String, Comment> comments = commentBatchStream.flatMapValues(batch -> batch.getComments()).selectKey((key, comment) -> comment.getCommentId());

        KStream<String, Comment> commentsWithLanguage = comments.transform(new RetryTransformerSupplier(), "inmemory-order-create-retry");

        Predicate<String, Comment> isEnglish = (key, comment) -> "en".equalsIgnoreCase(comment.getLanguage());

        KStream<String, Comment>[] branches = commentsWithLanguage.branch(isEnglish, (key, value) -> !isEnglish.test(key, value));

        KStream<String, Comment> english_stream = branches[0];
        KStream<String, Comment> non_english_stream = branches[1];

        KStream<String, Comment> translatable_comments_stream = non_english_stream.filter((key, comment) -> CommentTranslator.availableLanguages.contains(comment.getLanguage()));

        translatable_comments_stream.filter((s, comment) -> !CommentTranslator.availableLanguages.contains(comment.getLanguage())).to("untranslatable-comments");

        KStream<String, Comment> translated_stream = non_english_stream.transform(() -> new CommentTranslator());

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
                                        as("comment-counts")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.Languages()));

        languagesKTable.toStream().peek((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
        //Counting the comments grouped by videoId and language

        System.out.println(languagesKTable.queryableStoreName());
        languageStore = languagesKTable.queryableStoreName();

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

        cache.put(comment,language);

        return language;

        }

    private static KStream<String, Comment> filterComments(KStream<String, Comment> comments) {
        return comments.filter((key, comment) -> {
            for (String word : FILTERED_WORDS) {
                if (comment.getComment().contains(word)) {
                    return false;
                }
            }
            // Check if the comment contains any emojis
            if (EMOJI_PATTERN.matcher(comment.getComment()).find()) {
                return false;
            }
            return true;
        });
    }
}

