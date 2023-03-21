package ch.unisg.senty.scraper.flow;

import ch.unisg.senty.order.messages.MessageSender;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FindScraperAdapter implements JavaDelegate {

    private HashMap<String, String> scraperList = new HashMap<>();
    @Autowired
    private MessageSender messageSender;

    public FindScraperAdapter() {
        //Hard code scraper for now. First field would be type the other the "id" of the specific scraper
        scraperList.put("YouTube", "yt-scraper1");
    }

    @Override
    public void execute(DelegateExecution context) throws Exception {

        System.out.println("Order Successful");

        context.setVariable("target-scraper", scraperList.get("YouTube"));
    }
}
