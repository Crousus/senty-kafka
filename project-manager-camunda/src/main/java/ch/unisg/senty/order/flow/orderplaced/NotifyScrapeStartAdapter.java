package ch.unisg.senty.order.flow.orderplaced;

import ch.unisg.senty.order.domain.Order;
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

    Order order = (Order) context.getVariable("order");

    messageSender.send( //
        new Message<Order>( //
            "ScrapeStartEvent", //
            traceId, //
            order));
  }

  

}
