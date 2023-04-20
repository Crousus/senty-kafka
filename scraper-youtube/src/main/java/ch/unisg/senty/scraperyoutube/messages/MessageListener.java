package ch.unisg.senty.scraperyoutube.messages;

import ch.unisg.senty.scraperyoutube.application.ScraperService;
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

    @Autowired
    private MessageSender messageSender;

    @KafkaListener(id = "scraper-youtube", topics = MessageSender.TOPIC_NAME)
    public void goodsFetchedEventReceived(String messageJson, @Header("type") String messageType) throws Exception {
        System.out.println("Received message: " + messageJson);

        if ("PingYouTubeScraperCommand".equals(messageType)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Message message = new Message("YouTubeScraperAvailableEvent");
            messageSender.send(message);
        }

        if ("VerifyUrlCommand".equals(messageType)) {

            Message<VerifyUrlCommandPayload> message = objectMapper.readValue(messagePayloadJson, new TypeReference<Message<FetchGoodsCommandPayload>>() {});

            FetchGoodsCommandPayload fetchGoodsCommand = message.getData();
            String pickId = inventoryService.pickItems( //
                    fetchGoodsCommand.getItems(), fetchGoodsCommand.getReason(), fetchGoodsCommand.getRefId());

        }

        if ("TopUpTokensCommand".equals(messageType)) {
            System.out.println("TopUpTokensCommand received");
        }
    }

}
