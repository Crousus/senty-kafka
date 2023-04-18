package io.flowing.retail.payment.messages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageListener {

  @Autowired
  private MessageSender messageSender;

  @Autowired
  private ProcessEngine camunda;

  @Autowired
  private ObjectMapper objectMapper;

  @Transactional
  @KafkaListener(id = "payment", topics = MessageSender.TOPIC_NAME)
  public void messageReceived(String messagePayloadJson, @Header("type") String messageType) throws Exception{
    if (!"RetrievePaymentCommand".equals(messageType)) {
      System.out.println("Ignoring message of type " + messageType);
      return;
    }

    Message<RetrievePaymentCommandPayload> message = objectMapper.readValue(messagePayloadJson, new TypeReference<Message<RetrievePaymentCommandPayload>>(){});
    RetrievePaymentCommandPayload retrievePaymentCommand = message.getData();

    System.out.println("Retrieve payment: " + retrievePaymentCommand.getAmount() + " for " + retrievePaymentCommand.getRefId());

    camunda.getRuntimeService().createMessageCorrelation(message.getType()) //
            .processInstanceBusinessKey(message.getTraceid())
            .setVariable("amount", retrievePaymentCommand.getAmount()) //
            .setVariable("remainingAmount", retrievePaymentCommand.getAmount()) //
            .setVariable("refId", retrievePaymentCommand.getRefId()) //
            .setVariable("correlationId", message.getCorrelationid()) //
            .correlateWithResult();
  }



}
