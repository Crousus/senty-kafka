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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.HashSet;

@Component
public class MessageListener {
    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @KafkaListener(id = "email-notifier", topics = MessageSender.TOPIC_NAME)
    public void eventReceived(String messageJson, @Header("type") String messageType) throws Exception {
        logger.debug(messageJson);
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
        Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
        });
        IntNode payload = (IntNode) message.getData();
        int count = payload.intValue();

        //This is old and will play a part in the second part of the project

        emailService.sendEmail("Comment Milestone reached! You now have " + count + " comments on your video!", "johannesandreas.wenz@student.unisg.ch");
    }

    private void registrationSucceededHandler(String messageJson) throws JsonProcessingException {
        Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
        });

        String payload = message.getData().get("email").asText();

        //payload is also recepient
        emailService.sendEmail("Congrats your account " + payload + " is verified!", payload);
    }

    private void verifyEmailHandler(String messageJson) throws JsonProcessingException {
        Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
        });

        logger.debug(messageJson);
        String payload = message.getData().get("mailContent").asText();
        String recepient = message.getData().get("email").asText();

        emailService.sendEmail("Please verify at http://localhost:8096/verify?" + payload, recepient);
    }

    private void handleTopUpEvent(String messageJson) throws JsonProcessingException {
        Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
        });

        String payload = message.getTraceid();
        String recipient = message.getData().get("email").asText();

        emailService.sendEmail("Your TopUp order was fulfilled: " + payload, recipient);
    }

}
