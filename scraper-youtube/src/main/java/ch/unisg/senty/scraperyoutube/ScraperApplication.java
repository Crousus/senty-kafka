package ch.unisg.senty.scraperyoutube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScraperApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ScraperApplication.class, args);
  }

}
