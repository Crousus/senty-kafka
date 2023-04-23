package ch.unisg.senty.messages;

import ch.unisg.senty.domain.Order;
import ch.unisg.senty.domain.OrderStatus;
import ch.unisg.senty.repositoy.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class MessageListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(id = "scraper-youtube", topics = MessageSender.TOPIC_NAME)
    public void messageReceived(String messageJson, @Header("type") String messageType) throws Exception {
        System.out.println("Received message: " + messageJson);

        JsonNode jsonNode = objectMapper.readTree(messageJson);
        System.out.println(jsonNode);
        String traceId = jsonNode.get("traceid").asText();
        OrderStatus status = OrderStatus.CREATED;

        switch (messageType) {
            case "AuthenticationOutcomeEvent":
                System.out.println(messageJson);
                boolean auth = jsonNode.get("data").get("loginSuccessful").asBoolean();

                if (auth)
                    status = OrderStatus.AUTHENTICATED;

                break;
            case "OrderVerifiedEvent":
                String verified = jsonNode.get("data").get("title").asText();
                if (!verified.equals("false"))
                    status = OrderStatus.VERIFIED;

                break;
            case "FullfillOrderCommand":
                //Currently the happy path. In the future here will be a real order paid event or sth
                status = OrderStatus.PAID;
                break;
            case "ScrapeStartEvent":
                status = OrderStatus.FULFILLED;
                break;
            default:
                System.out.println("Unknown message type: " + messageType);
                return;
        }

        System.out.println(messageJson);

        Optional<Order> order = orderRepository.findById(traceId);

        if (!order.isPresent()) {
            System.out.println("Order not found");
            return;
        }

        Order foundOrder = order.get();
        foundOrder.setStatus(status);
        orderRepository.save(foundOrder);
    }
}
