package ch.unisg.senty.order.flow.findscrapers;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AggregateScrapersAdapter implements JavaDelegate {
    private static final Logger logger = LoggerFactory.getLogger(AggregateScrapersAdapter.class);
    @Override
    public void execute(DelegateExecution context) throws Exception {
        //Since the aggregation is now just a theory we don't do anything here yet
        logger.debug("AggregateScrapersAdapter");
    }
}
