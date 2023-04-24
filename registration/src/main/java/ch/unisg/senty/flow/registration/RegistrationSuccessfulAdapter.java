package ch.unisg.senty.flow.registration;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.Message;
import ch.unisg.senty.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationSuccessfulAdapter implements JavaDelegate {
  
  @Autowired
  private MessageSender messageSender;

  @Override
  public void execute(DelegateExecution context) throws Exception {
    String traceId = context.getProcessBusinessKey();

    Customer customer = (Customer) context.getVariable("customer");

    System.out.println("Order Successful");
    //TODO: Send Command to Email Service
    messageSender.send( //
        new Message<Customer>( //
            "OrderSuccessfulEvent", //
            traceId, //
            customer));
  }

  

}
