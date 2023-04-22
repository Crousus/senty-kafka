package ch.unisg.senty.messages;

import ch.unisg.senty.domain.Customer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.spin.plugin.variable.SpinValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class MessageListener {

  
  @Autowired
  private ProcessEngine camunda;

  @Autowired
  private ObjectMapper objectMapper;
  
  /**
   * Handles incoming OrderPlacedEvents. 
   *
   */
  @Transactional
  public void orderPlacedReceived(Message<Customer> message) throws JsonParseException, JsonMappingException, IOException {
    // persist domain entit

    Customer customer = message.getData();
    System.out.println("New order placed, start flow. " + customer);

    camunda.getRuntimeService().createMessageCorrelation(message.getType())
            .processInstanceBusinessKey(message.getTraceid()).setVariable("customer", customer)
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
  @KafkaListener(id = "authentication-service", topics = MessageSender.TOPIC_NAME)
  public void messageReceived(String messagePayloadJson, @Header("type") String messageType) throws Exception{

    System.out.println("Message received: " + messageType);
    if ("AuthenticationRequestCommand".equals(messageType)) {
      orderPlacedReceived(objectMapper.readValue(messagePayloadJson, new TypeReference<Message<Customer>>() {}));
    }
    Message<JsonNode> message = objectMapper.readValue( //
        messagePayloadJson, //
        new TypeReference<Message<JsonNode>>() {});
    
    long correlatingInstances = camunda.getRuntimeService().createExecutionQuery() //
      .messageEventSubscriptionName(message.getType()) //
      .processInstanceBusinessKey(message.getTraceid()) //
      .count();
    
    if (correlatingInstances==1) {
      System.out.println("Correlating " + message + " to waiting flow instance");
      
      camunda.getRuntimeService().createMessageCorrelation(message.getType())
        .processInstanceBusinessKey(message.getTraceid())
        .setVariable(//
            "PAYLOAD_" + message.getType(), // 
            SpinValues.jsonValue(message.getData().toString()).create())//
        .correlateWithResult();
    }
  }

}
