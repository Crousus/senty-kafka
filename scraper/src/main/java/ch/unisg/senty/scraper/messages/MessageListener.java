package ch.unisg.senty.scraper.messages;

import ch.unisg.senty.scraper.application.ScraperService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    @Autowired
    private ScraperService scraperService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(id = "scraper-yt", topics = MessageSender.TOPIC_NAME)
    public void goodsFetchedEventReceived(String messageJson, @Header("type") String messageType) throws Exception {
        System.out.println("Received message: " + messageJson);

        if ("TopUpTokensCommand".equals(messageType)) {

            System.out.println("TopUpTokensCommand received");
            // Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
            // });
            // IntNode payload = (IntNode) message.getData();
            // int count = payload.intValue();
        }
    }

}
