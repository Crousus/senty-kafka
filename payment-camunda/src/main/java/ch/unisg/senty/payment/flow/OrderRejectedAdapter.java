package ch.unisg.senty.payment.flow;

import ch.unisg.senty.payment.domain.Order;
import ch.unisg.senty.payment.messages.Message;
import ch.unisg.senty.payment.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderRejectedAdapter implements JavaDelegate {

    @Autowired
    private MessageSender messageSender;

    Logger logger = LoggerFactory.getLogger(OrderRejectedAdapter.class);

    @Override
    public void execute(DelegateExecution context) throws Exception {
        String traceId = context.getProcessBusinessKey();
        Order order = (Order) context.getVariable("order");
        String reason = "";

        if (context.getVariable("is_voucher_valid") != null && !(boolean) context.getVariable("is_voucher_valid"))
            reason = "Invalid voucher code";
         else reason = "Please update your payment information";

        logger.info("Order rejected! " + reason);

        messageSender.send( //
                new Message<Order>( //
                        "OrderRejectedEvent", //
                        traceId, //
                        order));

    }
}
