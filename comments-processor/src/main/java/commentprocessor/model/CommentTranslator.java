package commentprocessor.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import commentprocessor.TranslateRequest;
import kong.unirest.ContentType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;

import javax.management.ObjectName;
import java.util.*;

public class CommentTranslator implements Transformer<byte[], Comment, KeyValue<byte[], Comment>> {
    private ProcessorContext context;
    private static final String TRANSLATE_URI = "http://localhost:5000/translate";
    private HttpClient httpClient;

    public CommentTranslator() {
        this.httpClient = HttpClient.create();
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public KeyValue<byte[], Comment> transform(byte[] key, Comment value) {
        try {

            Map<String, String> translateRequestBody = new HashMap<>();
            translateRequestBody.put("q", value.getComment());
            translateRequestBody.put("source", value.getLanguage());
            translateRequestBody.put("target", "en");

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(translateRequestBody);
            System.out.println("Request body: " + requestBody);

            HttpResponse<String> response = Unirest.post(TRANSLATE_URI)
                    .header("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                    .body(requestBody)
                    .asString();

            JsonNode rootNode = mapper.readTree(response.getBody().toString());
            String translatedText = rootNode.get("translatedText").asText();
            value.setComment(translatedText);

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
