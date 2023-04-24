package ch.unisg.senty.order.flow.orderplaced;

import com.fasterxml.jackson.databind.JsonNode;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.springframework.stereotype.Service;

@Service
public class EvaluateScraperResponseAdapter implements JavaDelegate {
    @Override
    public void execute(DelegateExecution context) throws Exception {
        JacksonJsonNode response = (JacksonJsonNode) context.getVariable("PAYLOAD_OrderVerifiedEvent");
        System.out.println(response);


        //when the field is set to "title" = false then the order is not verified
        String title = response.prop("title").stringValue();

        context.setVariable("orderVerified", !title.equals("false"));
    }
}
