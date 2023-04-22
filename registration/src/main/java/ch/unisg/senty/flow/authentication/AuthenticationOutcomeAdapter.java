package ch.unisg.senty.flow.authentication;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.Message;
import ch.unisg.senty.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationOutcomeAdapter implements JavaDelegate {

    @Autowired
    private MessageSender messageSender;

    @Override
    public void execute(DelegateExecution context) throws Exception {
        String traceId = context.getProcessBusinessKey();

        Customer customer = (Customer) context.getVariable("customer");
        boolean loginSuccessful = (boolean) context.getVariable("loginSuccessful");

        messageSender.send( //
                new Message<AuthenticationOutcomeAdapterEventPayload>( //
                        "AuthenticationOutcomeEvent", //
                        traceId, //
                        new AuthenticationOutcomeAdapterEventPayload() //
                                .setCustomer(customer)
                                .setLoginSuccessful(loginSuccessful)));
    }
}
