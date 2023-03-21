package ch.unisg.senty.order;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class ProjectManagerCamundaApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ProjectManagerCamundaApplication.class, args);
  }
}
