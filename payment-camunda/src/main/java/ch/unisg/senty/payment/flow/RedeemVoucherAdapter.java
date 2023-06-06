package ch.unisg.senty.payment.flow;

import ch.unisg.senty.payment.domain.Order;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class RedeemVoucherAdapter implements JavaDelegate {


    @Override
    public void execute(DelegateExecution context) throws Exception {

        context.getVariables().forEach((s, o) -> System.out.println(s + " "+o));

        Order order = (Order) context.getVariable("order");
        String voucher = order.getVoucher();

        System.out.println();

        //If the voucher code does not match the expected one, the order will be rejected
        if (!voucher.equalsIgnoreCase("edpo")) {
            context.setVariable("is_voucher_valid", false);
            throw new BpmnError("invalid_voucher");
        }

        context.setVariable("is_voucher_valid", true);

    }
}
