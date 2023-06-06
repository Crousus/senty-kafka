package ch.unisg.senty.payment.messages;

import ch.unisg.senty.payment.domain.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.feel.syntaxtree.LessOrEqual;
import org.camunda.spin.plugin.variable.SpinValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageListener {

  Logger logger = LoggerFactory.getLogger(MessageListener.class);

  @Autowired
  private MessageSender messageSender;

  @Autowired
  private ProcessEngine camunda;

  @Autowired
  private ObjectMapper objectMapper;

  @Transactional
  @KafkaListener(id = "payment", topics = MessageSender.TOPIC_NAME)
  public void messageReceived(String messagePayloadJson, @Header("type") String messageType) throws Exception{
    if (!"FullfillOrderCommand".equals(messageType)) {
      logger.info("Ignoring message of type " + messageType);
      return;
    }

    Message<Order> message = objectMapper.readValue(messagePayloadJson, new TypeReference<Message<Order>>() {});
    Order order = message.getData();

    logger.info("Fulfill: " + order);

    camunda.getRuntimeService().createMessageCorrelation(message.getType()) //
            .processInstanceBusinessKey(message.getTraceid())
            .setVariable("order", order) //
            .setVariable("voucher", order.getVoucher() != null || !order.getVoucher().isEmpty()) //")
            .setVariable("correlationId", message.getCorrelationid()) //
            .correlateWithResult();
  }



}
