package commentprocessor.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kong.unirest.ContentType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import reactor.netty.http.client.HttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentimentAnalyzer implements Transformer<byte[], Comment, KeyValue<byte[], Comment>> {

    private ProcessorContext context;
    private static final String SENTIMENT_URI = "http://localhost:5001/model/predict";
    private HttpClient httpClient;

    public SentimentAnalyzer() {
        this.httpClient = HttpClient.create();
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public KeyValue<byte[], Comment> transform(byte[] key, Comment value) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode root = mapper.createObjectNode();
            root.set("text", mapper.createArrayNode().add(value.getComment()));
            String requestBody = mapper.writeValueAsString(root);
            System.out.println("Request body: " + requestBody);

            HttpResponse<String> response = Unirest.post(SENTIMENT_URI)
                    .header("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                    .body(requestBody)
                    .asString();

            System.out.println(response.getBody().toString());
            JsonNode rootNode = mapper.readTree(response.getBody().toString());
            JsonNode predictionsNode = rootNode.get("predictions");
            JsonNode firstPredictionNode = predictionsNode.get(0);

            double positive = firstPredictionNode.get("positive").asDouble();

            value.setSentimentScore(positive);

            return KeyValue.pair(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return KeyValue.pair(key, value);  // return original value on failure
        }
    }

    @Override
    public void close() {
    }
}
