package ch.unisg.senty.payment.rest;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class ChargeCreditCardAdapter implements JavaDelegate {

  public void execute(DelegateExecution ctx) throws Exception {

  }


}
