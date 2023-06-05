package ch.unisg.senty.order.flow;

import ch.unisg.senty.order.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class VerifyInputsAdapter implements JavaDelegate {

    @Autowired
    private MessageSender messageSender;

    private static final Logger logger = LoggerFactory.getLogger(VerifyInputsAdapter.class);

    @Override
    public void execute(DelegateExecution context) throws Exception {
        String traceId = context.getProcessBusinessKey();


        context.setVariable("useVoucher", true);
        logger.info("Input check passed");
    }
}
