package com.seedtoserve.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.seedtoserve.model.Mail;
import com.seedtoserve.repository.MailRepository;

@Service
public class MailService {

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    
    //Sends role-based welcome emails and logs them in DB
    
    public void sendAndLogEmail(String to, String name, String registrationType) {

        Mail mail = new Mail();
        mail.setSender("seedtoservewebapp@gmail.com");
        mail.setRecipient(to);
        mail.setSentAt(LocalDateTime.now());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("seedtoservewebapp@gmail.com");
            message.setTo(to);

            String subject;
            String body;

            
            if ("FARMER".equalsIgnoreCase(registrationType)) {
                subject = "Welcome to SeedToServe, Farmer!";
                body = "Hello " + name + ",\n\n"
                        + "Thank you for joining SeedToServe as a valued Farmer!\n\n"
                        + "You can now list your fresh produce, manage your categories, "
                        + "and connect directly with customers who trust your work.\n\n"
                        + "Let's grow together and make farming more rewarding.\n\n"
                        + "Warm regards,\n"
                        + "The SeedToServe Team\n"
                        + "seedtoserve";
            } else if ("BUYER".equalsIgnoreCase(registrationType)) {
                subject = "Welcome to SeedToServe, Buyer!";
                body = "Hello " + name + ",\n\n"
                        + "Thank you for joining SeedToServe as a Buyer!\n\n"
                        + "You can now explore a variety of fresh produce directly "
                        + "from our dedicated farmers. Enjoy fair prices and trustworthy sources.\n\n"
                        + "Happy shopping!\n\n"
                        + "Warm regards,\n"
                        + "The SeedToServe Team\n"
                        + "seedtoserve";
            } else {
                subject = "Welcome to SeedToServe!";
                body = "Hello " + name + ",\n\n"
                        + "Thank you for joining SeedToServe!\n\n"
                        + "Empowering Farmers, Enriching Lives.\n\n"
                        + "Warm regards,\n"
                        + "The SeedToServe Team\n"
                        + "seedtoserve";
            }

            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            mail.setSuccess(true);

        } catch (Exception e) {
            mail.setSuccess(false);
            System.err.println("Error sending mail: " + e.getMessage());
        }

        mailRepository.save(mail);
    }
}
