package ch.unisg.senty.order.flow.orderplaced;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TopUpTokensAdapter implements JavaDelegate {
  
  @Autowired
  private MessageSender messageSender;

  private static final Logger logger = LoggerFactory.getLogger(TopUpTokensAdapter.class);
  @Override
  public void execute(DelegateExecution context) throws Exception {
    String traceId = context.getProcessBusinessKey();

    logger.debug("TopUp Command placed");
    
    messageSender.send( //
        new Message<TopUpCommandPayload>( //
            "TopUpTokensCommand", //
            traceId, //
            new TopUpCommandPayload() //
              .setVideoId((String) context.getVariable("videoId")) //
              .setCustomerId((String) context.getVariable("customerId"))
              .setTokenAmount((String) context.getVariable("tokens"))));
  }

}
