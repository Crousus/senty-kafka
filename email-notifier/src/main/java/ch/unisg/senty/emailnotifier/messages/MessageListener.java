package ch.unisg.senty.emailnotifier.messages;

import ch.unisg.senty.emailnotifier.application.EmailService;
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
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(id = "email-notifier", topics = MessageSender.TOPIC_NAME)
    public void goodsFetchedEventReceived(String messageJson, @Header("type") String messageType) throws Exception {
        if ("CommentCountMilestoneEvent".equals(messageType)) {

            System.out.println(messageJson);
            Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>() {
            });
            IntNode payload = (IntNode) message.getData();
            int count = payload.intValue();

            emailService.sendEmail("Comment Milestone reached! You now have " + count + " comments on your video!");
        }
    }

}
