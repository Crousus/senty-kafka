package ch.unisg.senty.order.flow.orderplaced;

import com.fasterxml.jackson.databind.JsonNode;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EvaluateScraperResponseAdapter implements JavaDelegate {
    private static final Logger logger = LoggerFactory.getLogger(EvaluateScraperResponseAdapter.class);
    @Override
    public void execute(DelegateExecution context) throws Exception {
        JacksonJsonNode response = (JacksonJsonNode) context.getVariable("PAYLOAD_OrderVerifiedEvent");
        logger.debug(String.valueOf(response));


        //when the field is set to "title" = false then the order is not verified
        String title = response.prop("title").stringValue();

        context.setVariable("orderVerified", !title.equals("false"));
    }
}
