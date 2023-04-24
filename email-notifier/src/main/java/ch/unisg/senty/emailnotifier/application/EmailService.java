package ch.unisg.senty.emailnotifier.application;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class EmailService {

  public String sendEmail(String content, String recipient) {
    System.out.println("Sending Mail: " + content);

    Mailer mailer = MailerBuilder
            .withSMTPServerHost("smtp.mailgun.org")
            .withSMTPServerPort(587)
            .withSMTPServerUsername("postmaster@sandbox40cfeb661b37499fb93ad47f53c543a4.mailgun.org")
            .withSMTPServerPassword("b5b79ef6e37879273e1a58af945a64b7-15b35dee-82f37a84")
            .buildMailer();

    Email email = EmailBuilder.startingBlank()
            .from("postmaster@sandbox40cfeb661b37499fb93ad47f53c543a4.mailgun.org")
            .to("Our valuable Customer", recipient)
            .withSubject("Senty System Notification")
            .withPlainText(content)
            .buildEmail();

    mailer.sendMail(email);
    
    return UUID.randomUUID().toString();
  }

}
