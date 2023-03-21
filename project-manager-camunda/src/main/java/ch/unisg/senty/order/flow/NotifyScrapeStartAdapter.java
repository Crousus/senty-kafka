package ch.unisg.senty.order.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;

@Component
public class NotifyScrapeStartAdapter implements JavaDelegate {
  
  @Autowired
  private MessageSender messageSender;  

  @Override
  public void execute(DelegateExecution context) throws Exception {
    String traceId = context.getProcessBusinessKey();

    System.out.println("Scrape job placed Successful");

    messageSender.send( //
        new Message<ScrapeStartEventPayload>( //
            "ScrapeStartEvent", //
            traceId, //
            new ScrapeStartEventPayload() //
              .setOrderId("sdsad")));
  }

  

}
