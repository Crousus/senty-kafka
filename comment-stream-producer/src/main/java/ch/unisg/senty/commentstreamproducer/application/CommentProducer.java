package ch.unisg.senty.commentstreamproducer.application;

import ch.unisg.senty.commentstreamproducer.domain.Comment;
import com.google.common.io.Resources;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CommentProducer {
    public static void main(String[] args) throws Exception {

        String topic = "senty";

        Properties properties;
        try (InputStream props = Resources.getResource("producer.properties").openStream()) {
            properties = new Properties();
            properties.load(props);
        }

        KafkaProducer<String, Comment> producer =
                new KafkaProducer<>(properties);

        deleteTopic(topic, properties); // delete existing topic with the
        // same name

        createTopic(topic, 1, properties); // create new topic with 1 partition

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

                // send the comment event
                producer.send(new ProducerRecord<String, Comment>(
                        topic, // topic
                        commentEvent  // value
                ));

                // print to console
                System.out.println("commentEvent sent: "+commentEvent.toString());

                // Sleep for the specified time difference
                TimeUnit.SECONDS.sleep(timeDiffSec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void createTopic(String topicName, int numPartitions, Properties properties) throws Exception {

        AdminClient admin = AdminClient.create(properties);

        //checking if topic already exists
        boolean alreadyExists = admin.listTopics().names().get().stream()
                .anyMatch(existingTopicName -> existingTopicName.equals(topicName));
        if (alreadyExists) {
            System.out.printf("topic already exits: %s%n", topicName);
        } else {
            //creating new topic
            System.out.printf("creating topic: %s%n", topicName);
            NewTopic newTopic = new NewTopic(topicName, numPartitions, (short) 1);
            admin.createTopics(Collections.singleton(newTopic)).all().get();
        }
    }

    private static void deleteTopic(String topicName, Properties properties) {
        try (AdminClient client = AdminClient.create(properties)) {
            DeleteTopicsResult deleteTopicsResult = client.deleteTopics(Collections.singleton(topicName));
            while (!deleteTopicsResult.all().isDone()) {
                // Wait for future task to complete
            }
        }
    }
}
