package ch.unisg.senty.scraperyoutube.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MessageListenerVerify {
    @Value("${API-KEY}")
    private String apiKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSender messageSender;

    @KafkaListener(id = "scraper-youtube-verify", topics = Topics.WORKFLOW_CONSUMER_TOPIC)
    public void messageReceived(String messageJson, @Header("type") String messageType) throws Exception {
        if ("VerifyOrderCommand".equals(messageType)) {
            System.out.println("Received message: " + messageJson);
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            System.out.println(jsonNode);
            String traceId = jsonNode.get("traceid").asText();

            String url = jsonNode.get("data").asText();

            System.out.println("\nVerifyOrderCommand received: " + url);

            // e.g., https://www.youtube.com/watch?v=s_Nbg1tdDUA
            // e.g., https://youtu.be/s_Nbg1tdDUA
            Pattern pattern = Pattern.compile("(?<=v=|/videos/|/embed/|youtu\\.be/|/v/|/e/)[^#&?\\n]*");
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                // parse video ID
                String videoId = matcher.group();
                // System.out.println("Video ID: " + videoId);

                // fetch video data
                String videoData = fetchVideoData(videoId, apiKey);
                // System.out.println("Video Data: " + videoData);

                // check if length of "items" is 0
                JsonNode rootNode = objectMapper.readTree(videoData);
                JsonNode items = rootNode.get("items");
                if (items.size() == 0) {
                    System.out.println("Video ID not found");
                    // send event (without payload)
                    Message<String> message = new Message<String>("OrderVerifiedEvent");
                    message.setTraceid(traceId);
                    messageSender.send(message);
                    return;
                }

                System.out.println("Video ID found");

                Map<String, String> filteredData = filterVideoData(videoData);
                System.out.println("Filtered Data: " + filteredData);

                // TODO: change to "UrlVerificationSucceededEvent" and
                // "UrlVerificationFailedEvent"
                // send event
                Message<Map<String, String>> message = new Message<Map<String, String>>("OrderVerifiedEvent",
                        filteredData);
                message.setTraceid(traceId);
                messageSender.send(message);

            } else {
                System.out.println("Could not parse video ID");
                // send event (without payload)
                Message<Map<String, String>> message = new Message<Map<String, String>>("OrderVerifiedEvent");
                message.setTraceid(traceId);
                Map<String, String> data = new HashMap<>();
                data.put("title", "false");
                message.setData(data);
                messageSender.send(message);
            }
        }
    }

    private String fetchVideoData(String videoId, String apiKey) {
        WebClient webClient = WebClient.create();
        String url = "https://www.googleapis.com/youtube/v3/videos?id=" + videoId + "&part=snippet,statistics&key="
                + apiKey;

        Mono<String> response = webClient.get()
                .uri(url)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(String.class);

        return response.block();
    }

    private Map<String, String> filterVideoData(String videoData) throws IOException {
        // Deserialize JSON response
        JsonNode rootNode = objectMapper.readTree(videoData);

        // Extract desired fields
        JsonNode videoNode = rootNode.path("items").get(0);
        JsonNode snippetNode = videoNode.path("snippet");
        JsonNode statisticsNode = videoNode.path("statistics");

        String publishedAt = snippetNode.path("publishedAt").asText();
        String title = snippetNode.path("title").asText();
        String description = snippetNode.path("description").asText();
        String channelId = snippetNode.path("channelId").asText();
        String channelTitle = snippetNode.path("channelTitle").asText();
        String standardThumbnailUrl = snippetNode.path("thumbnails").path("standard").path("url").asText();

        String viewCount = statisticsNode.path("viewCount").asText();
        String likeCount = statisticsNode.path("likeCount").asText();
        String dislikeCount = statisticsNode.path("dislikeCount").asText();
        String favoriteCount = statisticsNode.path("favoriteCount").asText();
        String commentCount = statisticsNode.path("commentCount").asText();

        // Create a new HashMap and insert extracted fields
        Map<String, String> filteredData = new HashMap<>();
        // ###########################
        // TODO: Add videoId!!! filteredData.put("videoId", videoId);
        // ###########################
        filteredData.put("title", title);
        // filteredData.put("description", description);
        filteredData.put("channelId", channelId);
        filteredData.put("channelTitle", channelTitle);
        filteredData.put("publishedAt", publishedAt);
        filteredData.put("standardThumbnailUrl", standardThumbnailUrl);
        filteredData.put("viewCount", viewCount);
        filteredData.put("likeCount", likeCount);
        filteredData.put("dislikeCount", dislikeCount);
        filteredData.put("favoriteCount", favoriteCount);
        filteredData.put("commentCount", commentCount);

        return filteredData;
    }

}
