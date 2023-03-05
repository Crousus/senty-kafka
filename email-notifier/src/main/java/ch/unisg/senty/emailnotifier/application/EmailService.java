package ch.unisg.senty.emailnotifier.application;

import java.util.UUID;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.MailerGenericBuilder;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class EmailService {

  public String sendEmail(String content) {
    System.out.println("Sending Mail");

    Mailer mailer = MailerBuilder
            .withSMTPServerHost("smtp.mailgun.org")
            .withSMTPServerPort(587)
            .withSMTPServerUsername("postmaster@sandbox40cfeb661b37499fb93ad47f53c543a4.mailgun.org")
            .withSMTPServerPassword("<<PASSWORT>>")
            .buildMailer();

    Email email = EmailBuilder.startingBlank()
            .from("postmaster@sandbox40cfeb661b37499fb93ad47f53c543a4.mailgun.org")
            .to("Johannes Wenz", "johannesandreas.wenz@student.unisg.ch")
            .withSubject("Test Email from Spring service")
            .withPlainText("Hello this is a Test Mail from the Spring service")
            .buildEmail();

    mailer.sendMail(email);
    
    return UUID.randomUUID().toString();
  }

}
