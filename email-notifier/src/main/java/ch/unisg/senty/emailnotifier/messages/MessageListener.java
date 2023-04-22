package ch.unisg.senty.emailnotifier.messages;

import ch.unisg.senty.emailnotifier.application.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
public class MessageListener {
    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private HashSet<String> eventsAndCommands = new HashSet<>();

    public MessageListener() {
        Collections.addAll(eventsAndCommands,
                "CommentCountMilestoneEvent",
                "OrderRejectedEvent",
                "VerifyEmailCommand",
                "OrderSuccessfulEvent",
                "CustomerRegistrationSucceededEvent");
    }
    @KafkaListener(id = "email-notifier", topics = MessageSender.TOPIC_NAME)
    public void goodsFetchedEventReceived(String messageJson, @Header("type") String messageType) throws Exception {
        switch (messageType) {
            case "CommentCountMilestoneEvent":
                commentMileStoneHandler(messageJson);
                break;
            case "EmailVerifiedEvent":
                verifyEmailHandler(messageJson);
                break;
            case "CustomerRegistrationSucceededEvent":
                registrationSucceededHandler(messageJson);
                break;
            case "ScrapeStartEvent":
                handleTopUpEvent(messageJson);
                break;
            default:
                break;
        }

    }

    private void commentMileStoneHandler(String messageJson) throws JsonProcessingException {
        System.out.println(messageJson);
        Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
        });
        IntNode payload = (IntNode) message.getData();
        int count = payload.intValue();

        emailService.sendEmail("Comment Milestone reached! You now have " + count + " comments on your video!");
    }

    private void registrationSucceededHandler(String messageJson) throws JsonProcessingException {
        System.out.println(messageJson);
        Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
        });

        System.out.println(messageJson);
        String payload = message.getData().asText();

        emailService.sendEmail("Congrats your account " + payload + " is verified!");
    }

    private void verifyEmailHandler(String messageJson) throws JsonProcessingException {
        System.out.println(messageJson);
        Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
        });

        System.out.println(messageJson);
        String payload = message.getData().get("mailContent").asText();

        emailService.sendEmail("Please verify at http://localhost:8096/verify?" + payload);
    }

    private void handleTopUpEvent(String messageJson) throws JsonProcessingException {
        System.out.println(messageJson);
        Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
        });

        System.out.println(messageJson);
        String payload = message.getData().get("orderId").asText();

        emailService.sendEmail("Your TopUp order was fullfilled: " + payload);
    }

}
