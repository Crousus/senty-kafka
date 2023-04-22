package ch.unisg.senty.order.flow.orderplaced;

import ch.unisg.senty.order.messages.Message;
import ch.unisg.senty.order.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderRejectedAdapter implements JavaDelegate {

    @Autowired
    MessageSender messageSender;
    @Override
    public void execute(DelegateExecution context) throws Exception {
        String traceId = context.getProcessBusinessKey();

        messageSender.send( //
                new Message<>( //
                        "OrderRejectedEvent", //
                        traceId, //
                        "The order was rejected due to wrong inputs"));
    }
}
