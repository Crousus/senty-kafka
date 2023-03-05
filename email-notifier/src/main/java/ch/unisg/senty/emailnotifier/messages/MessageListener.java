package ch.unisg.senty.emailnotifier.messages;

import ch.unisg.senty.emailnotifier.application.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.unisg.senty.emailnotifier.messages.payload.NotificationEventPayload;

@Component
public class MessageListener {    
  
  @Autowired
  private MessageSender messageSender;

  @Autowired
  private EmailService emailService;

  @Autowired
  private ObjectMapper objectMapper;

  @Transactional
  @KafkaListener(id = "notification", topics = MessageSender.TOPIC_NAME)
  public void goodsFetchedEventReceived(String messageJson, @Header("type") String messageType) throws Exception {
    if ("NotificationEvent".equals(messageType)) {
      Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>(){});
      ObjectNode payload = (ObjectNode) message.getData();
      NotificationEventPayload payloadEvent = objectMapper //
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) //
              .treeToValue(payload, NotificationEventPayload.class);

      emailService.sendEmail(payloadEvent.getEmailContent());
    }
  }
    
}
