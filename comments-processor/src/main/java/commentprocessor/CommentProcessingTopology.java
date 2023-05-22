package commentprocessor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import commentprocessor.model.Comment;
import commentprocessor.model.CommentBatchEvent;
import commentprocessor.model.ScoreEvent;
import commentprocessor.serialization.json.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Printed;

import java.util.Arrays;

public class CommentProcessingTopology {

    public static Topology build() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<byte[], CommentBatchEvent> commentBatchStream =
                builder
                        .stream("new-comment-batches", Consumed.with(Serdes.ByteArray(), JsonSerdes.CommentBatch()));

        KStream<byte[], Comment> comments = commentBatchStream.flatMapValues(batch -> batch.getComments());

        Predicate<byte[], Comment> isEnglish = (key, comment) -> {
            String response = predictLanguage(comment.getComment());
            // Parse the response to get the language
            // Here Gson is used for parsing, replace with your chosen JSON library
            String language = new Gson().fromJson(response, JsonObject.class).get("language").getAsString();
            System.out.println("Language: " + language);
            return "english".equalsIgnoreCase(language);
        };

        KStream<byte[], Comment>[] branches = comments.branch(isEnglish, (key, value) -> !isEnglish.test(key, value));

        branches[0].foreach((key, value) -> System.out.println("Key: English" + Arrays.toString(key) + ", Value: " + value));
        branches[1].foreach((key, value) -> System.out.println("Key: Spanish" + Arrays.toString(key) + ", Value: " + value));

        return builder.build();
    }

    private static String predictLanguage(String comment) {
        if (Math.random() < 0.5) {
            return "{\"language\": \"spanish\"}";
        }
        return "{\"language\": \"english\"}";
    }
}
