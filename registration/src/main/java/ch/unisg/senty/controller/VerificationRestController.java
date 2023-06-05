package ch.unisg.senty.controller;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.Message;
import ch.unisg.senty.messages.MessageSender;
import ch.unisg.senty.messages.utils.WorkflowLogger;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class VerificationRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RuntimeService runtimeService;

    @RequestMapping(path = "/verify", method = RequestMethod.GET)
    public String verify(@RequestParam String email, @RequestParam String traceId) throws Exception{

        WorkflowLogger.info(logger, "Verification received",
                " for customer: " + email);

        try {

            Execution execution = runtimeService.createExecutionQuery()
                    .messageEventSubscriptionName("VerifyEmailEvent")
                    .processInstanceId(traceId)
                    .singleResult();

            Customer customer = ((Customer) runtimeService.getVariable(execution.getId(), "customer"));

            customer.setMailVerified(true);

            runtimeService.setVariable(execution.getId(), "customer", customer);

            runtimeService.messageEventReceived("VerifyEmailEvent", execution.getId());

            return "{\"status\":\"completed\", \"traceId\": \"" + execution.getId() + "\"}";
        } catch (Exception e) {
            WorkflowLogger.error(logger, "Verification failed",
                    " for customer: " + email + " with error: " + e.getMessage());
            return "{\"status\":\"not updated\"}";
        }




    }
}
