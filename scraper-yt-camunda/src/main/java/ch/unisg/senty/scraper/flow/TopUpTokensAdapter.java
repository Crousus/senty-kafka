package ch.unisg.senty.scraper.flow;

import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopUpTokensAdapter implements JavaDelegate {
  
  @Autowired
  private MessageSender messageSender;  


  @Override
  public void execute(DelegateExecution context) throws Exception {
    String traceId = context.getProcessBusinessKey(); 
    
    messageSender.send( //
        new Message<TopUpCommandPayload>( //
            "TopUpCommand", //
            traceId, //
            new TopUpCommandPayload() //
              .setVideoId((String) context.getVariable("videoId")) //
              .setCustomerId((String) context.getVariable("customerId"))
              .setTokenAmount((Integer) context.getVariable("tokenAmount"))));
  }

}
