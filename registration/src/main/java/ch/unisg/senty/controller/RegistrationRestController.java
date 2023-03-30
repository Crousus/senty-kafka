package ch.unisg.senty.controller;

import ch.unisg.senty.domain.Customer;
import ch.unisg.senty.messages.utils.WorkflowLogger;
import org.camunda.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

/**
 * @implSpec : Controller to start a new order process
 */
@RestController
public class RegistrationRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RuntimeService runtimeService;

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public String kickOffRegistration(@RequestBody Customer customer) throws Exception{

            String traceId = UUID.randomUUID().toString();

            WorkflowLogger.info(logger, "Payment received",
                    " for customer: " + customer.getCompany());

            HashMap<String, Object> variables = new HashMap<String, Object>();
            variables.put("customer", customer);


            runtimeService.startProcessInstanceByKey("registration",
                    traceId, variables);

            return "{\"status\":\"completed\", \"traceId\": \"" + traceId + "\"}";


    }

}
