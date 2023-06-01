package ch.unisg.senty.scraperyoutube.rest;

import ch.unisg.senty.scraperyoutube.messages.Message;
import ch.unisg.senty.scraperyoutube.messages.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class ScraperRestController {

  @Value("${API-KEY}")
  private String apiKey;

  @Autowired
  private ObjectMapper objectMapper;
  
  @Autowired
  private MessageSender messageSender;
  
  @GetMapping(path = "/api/scraperyoutube/ping")
  public ResponseEntity<String> ping() {
    Message message = new Message("PingYouTubeScraperCommand");

    messageSender.send(message);

    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

  @GetMapping(path = "/api/scraperyoutube/verify")
  public ResponseEntity<String> verify(@RequestParam String url) {
    Message<String> message = new Message<String>("VerifyOrderCommand", url);

    messageSender.send(message);

    System.out.println("\nVerifyOrderCommand received: " + url);

    // e.g., https://www.youtube.com/watch?v=s_Nbg1tdDUA
    // e.g., https://youtu.be/s_Nbg1tdDUA
    Pattern pattern = Pattern.compile("(?<=v=|/videos/|/embed/|youtu\\.be/|/v/|/e/)[^#&?\\n]*");
    Matcher matcher = pattern.matcher(url);
    Map<String, String> filteredData = null;
    if (matcher.find()) {
      // parse video ID
      String videoId = matcher.group();
      // System.out.println("Video ID: " + videoId);

      // fetch video data
      String videoData = fetchVideoData(videoId, apiKey);
      // System.out.println("Video Data: " + videoData);

      // check if length of "items" is 0
      JsonNode rootNode = null;
      try {
        rootNode = objectMapper.readTree(videoData);
      } catch (JsonProcessingException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid video ID");
      }
      JsonNode items = rootNode.get("items");
      if (items.size() == 0) {
        System.out.println("Video ID not found");
        // send event (without payload)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid video ID");
      }

      System.out.println("Video ID found");


      try {
        filteredData = filterVideoData(videoData);
      } catch (IOException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid video ID");
      }
      System.out.println("Filtered Data: " + filteredData);

    } else {
      System.out.println("Could not parse video ID");
      // send event (without payload)
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid video ID");
    }
    ObjectMapper objectMapper = new ObjectMapper();
    String responseJson = null;
    try {
      responseJson = "{\"traceId\": \"" + message.getTraceid() + "\", \"data\": " + objectMapper.writeValueAsString(filteredData) + "}";
    } catch (JsonProcessingException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid video ID");
    }
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

  @GetMapping(path = "/api/scraperyoutube/fetch")
  public ResponseEntity<String> fetch(@RequestParam String videoId) {
    Message<String> message = new Message<String>("FetchCommentsCommand",
            videoId);

    messageSender.send(message);
    System.out.println(message);
    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
  }

  @GetMapping(path = "/api/scraperyoutube/remove")
  public ResponseEntity<String> remove(@RequestParam String videoId) {
    Message<String> message = new Message<String>("RemoveVideoIdCommand",
            videoId);

    messageSender.send(message);

    String responseJson = "{\"traceId\": \"" + message.getTraceid() + "\"}";
    return ResponseEntity.status(HttpStatus.OK).body(responseJson);
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