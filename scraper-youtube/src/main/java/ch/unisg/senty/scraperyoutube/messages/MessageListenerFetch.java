package ch.unisg.senty.scraperyoutube.messages;

import ch.unisg.senty.scraperyoutube.domain.CommentBatchEvent;
import ch.unisg.senty.scraperyoutube.domain.CommentFetched;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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

    @Value("${senty.processing-topic-name}")
    private String processingTopicName;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public CopyOnWriteArrayList<String> videoIds = new CopyOnWriteArrayList<>();

    public Map<String, String> newestComments = new HashMap<>();

    private CommentBatchEvent buildBatch(String videoId) {
        // fetch comments
        List<CommentFetched> comments = fetchComments(videoId, apiKey, null);

        // create CommentBatchEvent
        CommentBatchEvent batchEvent = new CommentBatchEvent();
        batchEvent.setVideoId(videoId);
        batchEvent.setTimestamp(newestComments.get(videoId));
        batchEvent.setComments(comments);

        return batchEvent;
    }

    private void sendBatch(CommentBatchEvent batchEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(processingTopicName,  objectMapper.writeValueAsString(batchEvent));
            kafkaTemplate.send(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @KafkaListener(id = "scraper-youtube-fetch", topics = Topics.WORKFLOW_CONSUMER_TOPIC)
    public void messageReceived(String messageJson, @Header("type") String messageType) throws Exception {
        if ("FetchCommentsCommand".equals(messageType) || "TopUpTokensCommand".equals(messageType)) {
            System.out.println("Received message: " + messageJson);
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            System.out.println(jsonNode);
            String traceId = jsonNode.get("traceid").asText();
            String videoId = jsonNode.get("data").asText();

            //Since the format for the TopUpCommand is different, we need to check for that
            if (messageType.equals("TopUpTokensCommand")) {
                videoId = jsonNode.get("data").get("videoId").asText();
            }

            // e.g., "s_Nbg1tdDUA", "r0cM20WPyqI"

            System.out.println("\n"+messageType+" received: " + videoId);

            if (videoId == null || videoId.length() != 11) {
                System.out.println("No video ID provided");
                return;
            }
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
            CommentBatchEvent batchEvent = buildBatch(videoId);
            sendBatch(batchEvent);
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
            System.out.println("\nSCHEDULED comment fetch for video IDs: " + videoIds);

            for (String videoId : videoIds) {
                CommentBatchEvent batchEvent = buildBatch(videoId);
                sendBatch(batchEvent);
            }
        } else {
            System.out.println("\nNo video IDs to be scraped");
        }
    }

    private List<CommentFetched> fetchComments(String videoId, String apiKey, String pageToken) {
        System.out.println("Fetching comments for video ID: " + videoId);
        List<CommentFetched> comments = new ArrayList<>();

        WebClient webClient = WebClient.create();
        String url = "https://www.googleapis" +
                ".com/youtube/v3/commentThreads?part=snippet&videoId=" + videoId + "&key=" + apiKey + "&maxResults=100";

        if (pageToken != null) {
            url += "&pageToken=" + pageToken;
        }

        System.out.println("Fetching comments from: " + url);
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
            //Most of these are not used, but are here for possible future use
            String id = itemNode.path("id").asText();
            String textDisplay = itemNode.path("snippet").path("topLevelComment").path("snippet").path("textDisplay").asText();
            String textOriginal = itemNode.path("snippet").path("topLevelComment").path("snippet").path("textOriginal").asText();
            String authorDisplayName = itemNode.path("snippet").path("topLevelComment").path("snippet").path("authorDisplayName").asText();
            String authorProfileImageUrl = itemNode.path("snippet").path("topLevelComment").path("snippet").path("authorProfileImageUrl").asText();
            String authorChannelUrl = itemNode.path("snippet").path("topLevelComment").path("snippet").path("authorChannelUrl").asText();
            String authorChannelId = itemNode.path("snippet").path("topLevelComment").path("snippet").path("authorChannelId").path("value").asText();
            int likeCount = itemNode.path("snippet").path("topLevelComment").path("snippet").path("likeCount").asInt();
            String publishedAt = itemNode.path("snippet").path("topLevelComment").path("snippet").path("publishedAt").asText();
            String updatedAt = itemNode.path("snippet").path("topLevelComment").path("snippet").path("updatedAt").asText();
            System.out.println(publishedAt);

            comments.add(new CommentFetched(id, videoId, textDisplay, likeCount, publishedAt, null));
        }

        // get timestamps
        String newestCommentTimestamp = rootNode.path("items").get(0).path("snippet").path("topLevelComment").path("snippet").path("publishedAt").asText();
        System.out.println("Newest comment timestamp (api): " + newestCommentTimestamp);

        ZonedDateTime newestCommentApiTimestamp = ZonedDateTime.parse(newestCommentTimestamp);
        String newestCommentListTimestampStr = newestComments.get(videoId);

        // Check if we have a timestamp for this videoId
        if (newestCommentListTimestampStr == null || newestCommentListTimestampStr.isEmpty()) {
            // This is the first fetch for this videoId, update newestComments and continue fetching
            newestComments.put(videoId, newestCommentTimestamp);
            if (rootNode.has("nextPageToken")) {
                String nextPageToken = rootNode.get("nextPageToken").asText();
                comments.addAll(fetchComments(videoId, apiKey, nextPageToken));
            }
        } else {
            ZonedDateTime newestCommentListTimestamp = ZonedDateTime.parse(newestCommentListTimestampStr);

            // Update newest comment timestamp if new comment is newer
            if (newestCommentApiTimestamp.isAfter(newestCommentListTimestamp)) {
                newestComments.put(videoId, newestCommentTimestamp);
            }

            // if this is the first fetch or if the newest comment timestamp from the api is after the newest comment from the list, fetch the next page
            if (newestCommentApiTimestamp.isAfter(newestCommentListTimestamp)) {
                if (rootNode.has("nextPageToken")) {
                    String nextPageToken = rootNode.get("nextPageToken").asText();
                    comments.addAll(fetchComments(videoId, apiKey, nextPageToken));
                }
            }
            // else if the timestamps are equal, return an empty list
            else if (newestCommentApiTimestamp.isEqual(newestCommentListTimestamp)) {
                return new ArrayList<>();
            }
        }

        System.out.println("Newest comment timestamp (list): " + newestComments.get(videoId));

        return comments;
    }
}
