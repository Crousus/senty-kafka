package ch.unisg.senty.commentanalysis.messages;

import ch.unisg.senty.commentanalysis.domain.Comment;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class MessageListener {    
  
  @Autowired
  private MessageSender messageSender;

  @Autowired
  private ObjectMapper objectMapper;

  private int count;
  private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);



  @KafkaListener(id = "comment-analyzer", topics = MessageSender.TOPIC_NAME)
  public void orderPlaced(String messageJson, @Header("type") String messageType) throws Exception {
    if ("CommentAddedEvent".equals(messageType)) {
      logger.debug(messageJson);
      Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>(){});

      ObjectNode payload = (ObjectNode) message.getData();
      Comment comment = objectMapper.treeToValue(payload, Comment.class);

      // hardcoded milestones to avoid continuous spam
      int milestone1 = 10;
      int milestone2 = 30;
      int milestone3 = 100;

      if (count == milestone1 || count == milestone2 || count == milestone3) {

        messageSender.send(
                new Message<>(
                        "CommentCountMilestoneEvent",
                        comment.getCommentId(), //
                        count));
      }

      count++;
    }
  }
    
}
