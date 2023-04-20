package ch.unisg.senty.order.flow.findscrapers;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class AggregateScrapersAdapter implements JavaDelegate {
    @Override
    public void execute(DelegateExecution context) throws Exception {

    }
}
