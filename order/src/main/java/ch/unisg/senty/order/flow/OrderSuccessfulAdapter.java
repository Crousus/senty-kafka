package ch.unisg.senty.order.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;

@Component
public class OrderSuccessfulAdapter implements JavaDelegate {

  private static final Logger logger = LoggerFactory.getLogger(OrderSuccessfulAdapter.class);
  
  @Autowired
  private MessageSender messageSender;  

  @Override
  public void execute(DelegateExecution context) throws Exception {
    String traceId = context.getProcessBusinessKey();

    String customerId = (String) context.getVariable("customerId");
    String videoId = (String) context.getVariable("videoId");
    String token = (String) context.getVariable("tokens");

    logger.debug("Order Successful");

    messageSender.send( //
        new Message<OrderSuccessfulEventPayload>( //
            "OrderSuccessfulEvent", //
            traceId, //
            new OrderSuccessfulEventPayload() //
              .setCustomerId(customerId).setVideoId(videoId).setTokens(token)));
  }

  

}
