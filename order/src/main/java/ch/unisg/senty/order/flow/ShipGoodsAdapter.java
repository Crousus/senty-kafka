package ch.unisg.senty.order.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.unisg.senty.order.domain.Order;
import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;
import ch.unisg.senty.order.persistence.OrderRepository;

@Component
public class ShipGoodsAdapter implements JavaDelegate {
  
  @Autowired
  private MessageSender messageSender;  

  @Autowired
  private OrderRepository orderRepository;  

  @Override
  public void execute(DelegateExecution context) throws Exception {
    Order order = orderRepository.findById( //
        (String)context.getVariable("orderId")).get(); 
    String pickId = (String)context.getVariable("pickId"); // TODO read from step before!
    String traceId = context.getProcessBusinessKey();

    messageSender.send(new Message<ShipGoodsCommandPayload>( //
            "ShipGoodsCommand", //
            traceId, //
            new ShipGoodsCommandPayload() //
              .setRefId(order.getId())
              .setPickId(pickId) //
              .setRecipientName(order.getCustomer().getName()) //
              .setRecipientAddress(order.getCustomer().getAddress()))); 
  }  

}
