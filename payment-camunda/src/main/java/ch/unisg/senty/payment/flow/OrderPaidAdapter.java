package ch.unisg.senty.payment.flow;

import ch.unisg.senty.payment.domain.Order;
import ch.unisg.senty.payment.messages.Message;
import ch.unisg.senty.payment.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderPaidAdapter implements JavaDelegate {

  @Autowired
  private MessageSender messageSender;

  @Override
  public void execute(DelegateExecution context) throws Exception {
    Order order = (Order) context.getVariable("order");
    String traceId = context.getProcessBusinessKey();

    messageSender.send( //
        new Message<Order>( //
            "OrderPaidEvent", //
            traceId, //
            order));
  }

}
