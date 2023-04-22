package ch.unisg.senty.flow.registration;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.Message;
import ch.unisg.senty.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendMailVerificationCommandAdapter implements JavaDelegate {

    @Autowired
    private MessageSender messageSender;

    @Override
    public void execute(DelegateExecution context) throws Exception {
        String traceId = context.getProcessBusinessKey();

        Customer customer = (Customer) context.getVariable("customer");

        customer.getEmail();
        messageSender.send( //
                new Message<EmailVerificationCommandPayload>( //
                        "EmailVerifiedEvent", //
                        traceId, //
                        new EmailVerificationCommandPayload() //
                                .setEmail(customer.getEmail())
                                .setMailContent("email="+customer.getEmail()+"&traceId="+context.getProcessInstanceId())));
    }
}
