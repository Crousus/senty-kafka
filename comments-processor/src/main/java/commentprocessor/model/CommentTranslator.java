package commentprocessor.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.ContentType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import reactor.netty.http.client.HttpClient;

import java.util.*;

public class CommentTranslator implements Transformer<String, Comment, KeyValue<String, Comment>> {
    private ProcessorContext context;
    private static final String TRANSLATE_URI = "http://localhost:5000/translate";
    private HttpClient httpClient;

    public static Set<String> availableLanguages = Set.of(
            "ar",
            "az",
            "cs",
            "da",
            "de",
            "el",
            "en",
            "eo",
            "es",
            "fa",
            "fi",
            "fr",
            "ga",
            "he",
            "hi",
            "hu",
            "id",
            "it",
            "ja",
            "ko",
            "nl",
            "pl",
            "pt",
            "ru",
            "sk",
            "sv",
            "tr",
            "uk",
            "zh");

    public CommentTranslator() {
        this.httpClient = HttpClient.create();
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public KeyValue<String, Comment> transform(String key, Comment value) {
        JsonNode rootNode = null;
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

            rootNode = mapper.readTree(response.getBody().toString());
            String translatedText = rootNode.get("translatedText").asText();
            value.setComment(translatedText);

            return KeyValue.pair(key, value);
        } catch (Exception e) {
            System.out.println(rootNode.asText());
            System.out.println("Error translating comment: " + e.getMessage());
            return KeyValue.pair(key, value);  // return original value on failure
        }
    }

    @Override
    public void close() {
    }
}
