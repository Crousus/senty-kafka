package ch.unisg.senty.order.flow.orderplaced;

import ch.unisg.senty.order.domain.Order;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotifyScrapeStartAdapter implements JavaDelegate {
  
  @Autowired
  private MessageSender messageSender;
  private static final Logger logger = LoggerFactory.getLogger(NotifyScrapeStartAdapter.class);

  @Override
  public void execute(DelegateExecution context) throws Exception {
    String traceId = context.getProcessBusinessKey();

    logger.debug("Scrape job placed Successful");

    Order order = (Order) context.getVariable("order");

    messageSender.send( //
        new Message<Order>( //
            "ScrapeStartEvent", //
            traceId, //
            order));
  }

  

}
