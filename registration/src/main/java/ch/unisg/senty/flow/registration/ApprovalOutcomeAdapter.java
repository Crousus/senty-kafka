package ch.unisg.senty.flow.registration;

import ch.unisg.senty.domain.Customer;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApprovalOutcomeAdapter implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(DelegateExecution context) {

        Customer customer = ((Customer) context.getVariable("customer"));

        customer.setHumanApproved((Boolean) context.getVariable("approved"));

        context.setVariable("customer", customer);

    }
}
