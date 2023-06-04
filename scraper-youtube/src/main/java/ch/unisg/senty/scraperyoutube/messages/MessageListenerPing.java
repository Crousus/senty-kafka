package ch.unisg.senty.scraperyoutube.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class MessageListenerPing {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSender messageSender;

    @KafkaListener(id = "scraper-youtube-ping", topics = Topics.WORKFLOW_CONSUMER_TOPIC)
    public void messageReceived(String messageJson, @Header("type") String messageType) throws Exception {

        if ("PingYouTubeScraperCommand".equals(messageType)) {
            System.out.println("\nReceived message: " + messageJson);
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            System.out.println(jsonNode);
            String traceId = jsonNode.get("traceid").asText();

            System.out.println("PingYouTubeScraperCommand received");

            Message message = new Message("ScraperResponseEvent");

            message.setTraceid(traceId);
            messageSender.send(message);
        }
    }
}
