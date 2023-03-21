package ch.unisg.senty.scraper.flow;

import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotifyScrapeStartAdapter implements JavaDelegate {
  
  @Autowired
  private MessageSender messageSender;  

  @Override
  public void execute(DelegateExecution context) throws Exception {
    String orderId = (String)context.getVariable("orderId"); 
    String traceId = context.getProcessBusinessKey();

    System.out.println("Order Successful");

    messageSender.send( //
        new Message<ScrapeStartEventPayload>( //
            "OrderSuccessfulEvent", //
            traceId, //
            new ScrapeStartEventPayload() //
              .setOrderId(orderId)));
  }

  

}
