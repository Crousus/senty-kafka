package ch.unisg.senty.order.messages;

import java.io.IOException;

import ch.unisg.senty.order.domain.Order;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.spin.plugin.variable.SpinValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MessageListener {

  
  @Autowired
  private ProcessEngine camunda;

  @Autowired
  private ObjectMapper objectMapper;

  private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
  
  /**
   * Handles incoming OrderPlacedEvents. 
   *
   */
  @Transactional
  public void orderPlacedReceived(Message<Order> message) throws JsonParseException, JsonMappingException, IOException {
    // persist domain entit

    Order order = message.getData();
    logger.debug("New order placed, start flow. " + order);

    camunda.getRuntimeService().createMessageCorrelation(message.getType())
            .processInstanceBusinessKey(message.getTraceid()).setVariable("order", order)
            .correlateWithResult();
  }
  
  /**
   * Very generic listener for simplicity. It takes all events and checks, if a 
   * flow instance is interested. If yes, they are correlated, 
   * otherwise they are just discarded.
   *  
   * It might make more sense to handle each and every message type individually.
   */
  @Transactional
  @KafkaListener(id = "project-manager", topics = MessageSender.TOPIC_NAME)
  public void messageReceived(String messagePayloadJson, @Header("type") String messageType) throws Exception{
    if ("OrderPlacedEvent".equals(messageType) || "FullfillOrderCommand".equals(messageType)) {
      orderPlacedReceived(objectMapper.readValue(messagePayloadJson, new TypeReference<Message<Order>>() {}));
    }
    Message<JsonNode> message = objectMapper.readValue( //
        messagePayloadJson, //
        new TypeReference<Message<JsonNode>>() {});
    
    long correlatingInstances = camunda.getRuntimeService().createExecutionQuery() //
      .messageEventSubscriptionName(message.getType()) //
      .processInstanceBusinessKey(message.getTraceid()) //
      .count();
    
    if (correlatingInstances==1) {
      logger.debug("Correlating " + message + " to waiting flow instance");
      logger.debug(message.getData().toString());
      
      camunda.getRuntimeService().createMessageCorrelation(message.getType())
        .processInstanceBusinessKey(message.getTraceid())
        .setVariable(//
            "PAYLOAD_" + message.getType(), // 
            SpinValues.jsonValue(message.getData().toString()).create())//
        .correlateWithResult();

      camunda.getRuntimeService().createExecutionQuery().processInstanceBusinessKey(message.getTraceid()).list().forEach(e -> {
        camunda.getRuntimeService().getVariables(e.getId()).forEach((s, o) -> {
          logger.debug(s + " " + o);
        });

      });
    }
  }

}
