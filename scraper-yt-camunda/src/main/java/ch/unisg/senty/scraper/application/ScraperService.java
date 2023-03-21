package ch.unisg.senty.scraper.application;

import ch.unisg.senty.scraper.domain.Comment;
import ch.unisg.senty.scraper.messages.Message;
import ch.unisg.senty.scraper.messages.MessageSender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.FileReader;

import java.util.concurrent.TimeUnit;

@Component
public class ScraperService {

    @Autowired
    MessageSender messageSender;

    @Autowired
    private ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void startScraper() {

        try {
            // Open the file and parse the JSON data
            JSONParser parser = new JSONParser();
            JSONArray comments = (JSONArray) parser.parse(new FileReader("data/comments_filtered_merged_sorted_timed.json"));

            // Iterate through the comments array
            for (int i = 212; i < comments.size(); i++) { // skipping first
                // 212 comments because after that the time difference is short

                JSONObject commentObj = (JSONObject) comments.get(i);

                // Extract the values from the comment object
                String channelId = (String) commentObj.get("channelId");
                String channelName = (String) commentObj.get("channelName");
                String videoId = (String) commentObj.get("videoId");
                String videoName = (String) commentObj.get("videoName");
                String commentAuthorId = (String) commentObj.get("commentAuthorId");
                String commentAuthorName = (String) commentObj.get("commentAuthorName");
                String commentId = (String) commentObj.get("commentId");
                String commentText = (String) commentObj.get("commentText");
                String commentDateTime = (String) commentObj.get("commentDateTime");
                long timeDiffSec = ((Number) commentObj.get("timeDiffSec")).longValue();

                // Create a new Comment object using the extracted values
                Comment commentEvent = new Comment(channelId, channelName,
                        videoId, videoName, commentAuthorId, commentAuthorName,
                        commentId, commentText, commentDateTime, timeDiffSec);


                messageSender.send( //
                        new Message<>( //
                                "CommentAddedEvent", //
                                commentEvent.getCommentId(), //
                                objectMapper.convertValue(commentEvent, JsonNode.class)));

                // print to console
                System.out.println("commentEvent sent: " + commentEvent);

                // Sleep for the specified time difference
                TimeUnit.SECONDS.sleep(timeDiffSec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
