package ch.unisg.senty.order.flow.base;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.unisg.senty.order.domain.Order;
import ch.unisg.senty.order.flow.FetchGoodsCommandPayload;
import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;
import ch.unisg.senty.order.persistence.OrderRepository;

/**
 * Alternative implementation if you prefer having send/receive in one single ServiceTask
 * which is often easier understood by "normal people"
 *
 */
@Component
public class FetchGoodsPubSubAdapter extends PublishSubscribeAdapter {
  
  @Autowired
  private MessageSender messageSender;  

  @Autowired
  private OrderRepository orderRepository;  

  @Override
  public void execute(ActivityExecution context) throws Exception {
    Order order = orderRepository.findById( //
        (String)context.getVariable("orderId")).get(); 
    String traceId = context.getProcessBusinessKey();

    // publish
    messageSender.send(new Message<FetchGoodsCommandPayload>( //
            "FetchGoodsCommand", //
            traceId, //
            new FetchGoodsCommandPayload() //
              .setRefId(order.getId()) //
              .setItems(order.getItems())));
    
    addMessageSubscription(context, "GoodsFetchedEvent");
  }
  
}
