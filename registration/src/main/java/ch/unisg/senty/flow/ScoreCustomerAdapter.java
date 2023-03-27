package ch.unisg.senty.process;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ch.unisg.senty.utils.WorkflowLogger;

@Service("ScoreCustomerAdapter")
public class ScoreCustomerAdapter implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(DelegateExecution execution) {

        String company = (String) execution.getVariable("company");

        if (company.equals("Porsche")) { // change to find domain substring in email
            execution.setVariable("automaticProcessing", (boolean)true);
            WorkflowLogger.info(logger, "scoreCustomer","Customer is scored automatically");
        }
        else {
            execution.setVariable("automaticProcessing", (boolean)false);
            WorkflowLogger.info(logger, "scoreCustomer","Customer needs approval");
        }



    }

}