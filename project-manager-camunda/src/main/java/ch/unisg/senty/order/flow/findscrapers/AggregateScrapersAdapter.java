package ch.unisg.senty.order.flow.findscrapers;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class AggregateScrapersAdapter implements JavaDelegate {
    @Override
    public void execute(DelegateExecution context) throws Exception {
        //Since the aggregation is now just a theory we don't do anything here yet
        System.out.println("AggregateScrapersAdapter");
    }
}
