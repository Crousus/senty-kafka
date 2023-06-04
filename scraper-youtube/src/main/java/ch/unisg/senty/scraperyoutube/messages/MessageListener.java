package ch.unisg.senty.scraperyoutube.messages;

import ch.unisg.senty.scraperyoutube.application.ScraperService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageListener {
    @Autowired
    private ScraperService scraperService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSender messageSender;

    @KafkaListener(id = "scraper-youtubee", topics = Topics.WORKFLOW_CONSUMER_TOPIC)
    public void messageReceived(String messageJson, @Header("type") String messageType) throws Exception {
        if ("TopUpTokensCommand".equals(messageType)) {
            System.out.println("\nReceived message: " + messageJson);
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            System.out.println(jsonNode);
            String traceId = jsonNode.get("traceid").asText();

            System.out.println("TopUpTokensCommand received");

            Message<Map<String, String>> message = new Message<Map<String, String>>("JobStatusUpdateEvent");
            message.setTraceid(traceId);
            Map<String, String> data = new HashMap<>();
            data.put("jobstatus", "received");
            message.setData(data);
            messageSender.send(message);
        }
    }
}
