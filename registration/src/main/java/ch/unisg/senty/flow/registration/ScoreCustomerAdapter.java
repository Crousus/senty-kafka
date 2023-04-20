package ch.unisg.senty.flow.registration;

import ch.unisg.senty.domain.Customer;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ch.unisg.senty.messages.utils.WorkflowLogger;

@Service
public class ScoreCustomerAdapter implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(DelegateExecution context) {

        String company = ((Customer) context.getVariable("customer")).getCompany();

        if (company.equals("Porsche")) { // change to find domain substring in email
            context.setVariable("automaticProcessing", true);
            WorkflowLogger.info(logger, "scoreCustomer","Customer is scored automatically");
        }
        else {
            context.setVariable("automaticProcessing", false);
            WorkflowLogger.info(logger, "scoreCustomer","Customer needs approval");
        }



    }

}