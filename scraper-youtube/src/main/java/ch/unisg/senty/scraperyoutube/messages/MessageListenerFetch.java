package ch.unisg.senty.scraperyoutube.messages;

import ch.unisg.senty.scraperyoutube.domain.CommentFetched;
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
import java.time.ZonedDateTime;
import java.util.*;

import org.springframework.scheduling.annotation.Scheduled;
import java.util.concurrent.CopyOnWriteArrayList;


@Component
public class MessageListenerFetch {
    @Value("${API-KEY}")
    private String apiKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSender messageSender;

    public CopyOnWriteArrayList<String> videoIds = new CopyOnWriteArrayList<>();

    public Map<String, String> newestComments = new HashMap<>();

    @KafkaListener(id = "scraper-youtube-fetch", topics = MessageSender.TOPIC_NAME)
    public void messageReceived(String messageJson, @Header("type") String messageType) throws Exception {
        if ("FetchCommentsCommand".equals(messageType)) {
            System.out.println("Received message: " + messageJson);
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            System.out.println(jsonNode);
            String traceId = jsonNode.get("traceid").asText();

            String videoId = jsonNode.get("data").asText();
            // e.g., "s_Nbg1tdDUA", "r0cM20WPyqI"

            System.out.println("\nFetchCommentsCommand received: " + videoId);

            // add video ID to list of video IDs
            if (!videoIds.contains(videoId)) {
                videoIds.add(videoId);
            }

            // initialize newest comment for video ID
            if (!newestComments.containsKey(videoId)) {
                newestComments.put(videoId, "");
            }

            System.out.println("Video IDs in list: " + videoIds);

            // fetch comments for each video ID
            // in the future we would thread this...
            List<CommentFetched> comments = fetchComments(videoId, apiKey, null);

            // JOHANNES CHANGE THIS IF YOU ONLY WANT ONE BIG BLOCK, AMD NOT
            // FOR EACH
            for (CommentFetched commentFetched : comments) {
                Message<String> message = new Message<String>(
                        "CommentFetchedEvent", commentFetched.getText());
                message.setTraceid(traceId);
                messageSender.send(message);
            }
        }

        if ("RemoveVideoIdCommand".equals(messageType)) {
            System.out.println("\nReceived message: " + messageJson);
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            System.out.println(jsonNode);
            String traceId = jsonNode.get("traceid").asText();

            String videoId = jsonNode.get("data").asText();
            // e.g., "s_Nbg1tdDUA", "r0cM20WPyqI"
            System.out.println("\nRemoveVideoIdCommand received: " + videoId);

            if (videoIds.contains(videoId)) {
                videoIds.remove(videoId);
            }

            System.out.println("Video IDs to be scraped: " + videoIds);

            // TODO: Send message?
        }
    }

    @Scheduled(fixedRate = 20000)
    public void fetchCommentsPeriodically() {
        if (!videoIds.isEmpty()) {
            System.out.println("\nAutomatically fetching comments for video " +
                    "IDs: " + videoIds);
            // We use CopyOnWriteArrayList to avoid ConcurrentModificationException
            for (String videoId : videoIds) {
                List<CommentFetched> comments = fetchComments(videoId, apiKey, null);

                for (CommentFetched commentFetched : comments) {
                    Message<String> message = new Message<String>(
                            "CommentFetchedEvent", commentFetched.getText());
                    // We generate a new traceid for each fetch
                    String traceId = UUID.randomUUID().toString();
                    message.setTraceid(traceId);
                    messageSender.send(message);
                }
            }
        } else {
            System.out.println("\nNo video IDs to be scraped");
        }
    }

    private List<CommentFetched> fetchComments(String videoId, String apiKey,
                                  String pageToken) {
        System.out.println("Fetching comments for video ID: " + videoId);
        List<CommentFetched> comments = new ArrayList<>();

        WebClient webClient = WebClient.create();
        String url = "https://www.googleapis" +
                ".com/youtube/v3/commentThreads?part=snippet&videoId=" + videoId + "&key=" + apiKey + "&maxResults=100";

        if (pageToken != null) {
            url += "&pageToken=" + pageToken;
        }

        Mono<String> response = webClient.get()
                .uri(url)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(String.class);

        String responseString = response.block();

        // Parse JSON response
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(responseString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // print length of list
        System.out.println("Fetched " + rootNode.path("items").size() + " comments");

        // get comments
        for (JsonNode itemNode : rootNode.path("items")) {
            String commentText = itemNode.path("snippet").path("topLevelComment").path("snippet").path("textDisplay").asText();
            comments.add(new CommentFetched(commentText));
        }

        // get timestamps
        String newestCommentTimestamp = rootNode.path("items").get(0).path("snippet").path("topLevelComment").path("snippet").path("publishedAt").asText();
        System.out.println("Newest comment timestamp (api): " + newestCommentTimestamp);

        String oldestCommentTimestamp = rootNode.path("items").get(rootNode.path("items").size() - 1).path("snippet").path("topLevelComment").path("snippet").path("publishedAt").asText();
        System.out.println("Oldest comment timestamp (api): " + oldestCommentTimestamp);

        // Initial fetch should get all comments, otherwise fetch until newest
        if (newestComments.get(videoId).equals("")) {
            newestComments.put(videoId, newestCommentTimestamp);
            if (rootNode.has("nextPageToken")) {
                String nextPageToken = rootNode.get("nextPageToken").asText();
                comments.addAll(fetchComments(videoId, apiKey, nextPageToken));
            }
        } else {
            ZonedDateTime newestCommentApiTimestamp = ZonedDateTime.parse(newestCommentTimestamp);
            ZonedDateTime oldestCommentApiTimestamp = ZonedDateTime.parse(oldestCommentTimestamp);
            ZonedDateTime newestCommentListTimestamp = ZonedDateTime.parse(newestComments.get(videoId));

            // Update newest comment timestamp if new comment is newer
            if (newestCommentApiTimestamp.isAfter(newestCommentListTimestamp)) {
                newestComments.put(videoId, newestCommentTimestamp);
            }

            // Return empty list if timestamp did not change
            if (newestCommentApiTimestamp.isEqual(newestCommentListTimestamp)) {
                return new ArrayList<>(); // Return empty list
            }

            // Fetch more comments if oldestCommentApiTimestamp of new fetch is
            // still newer than newestCommentListTimestamp
            if (oldestCommentApiTimestamp.isAfter(newestCommentListTimestamp)) {
                if (rootNode.has("nextPageToken")) {
                    String nextPageToken = rootNode.get("nextPageToken").asText();
                    comments.addAll(fetchComments(videoId, apiKey, nextPageToken));
                }
            }
        }
        System.out.println("Newest comment timestamp (list): " + newestComments.get(videoId));
        return comments;
    }
}
