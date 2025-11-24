package com.seedtoserve.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.ContactDTO;
import com.seedtoserve.model.Contact;
import com.seedtoserve.repository.ContactRepository;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public ResponseEntity<String> createContact(ContactDTO contactDto) {

        Optional<Contact> isExistingDescription =
                contactRepository.findByDescription(contactDto.getDescription());

        if (isExistingDescription.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("This concern is already submitted. Our team is working on it!");
        }

        try {
         
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("seedtoservewebapplication@gmail.com");
            message.setFrom(contactDto.getEmail());
            message.setSubject("New Contact Form Submission");

            String content =
                    "📩 New Contact Form Submission\n\n" +
                    "👤 Name: " + contactDto.getFullName() + "\n" +
                    "📧 Email: " + contactDto.getEmail() + "\n" +
                    "📞 Phone: " + contactDto.getPhoneNumber() + "\n" +
                    "📝 Concern: " + contactDto.getDescription() + "\n";


            message.setText(content);

            Contact contact = new Contact();
            contact.setFullName(contactDto.getFullName());
            contact.setEmail(contactDto.getEmail());
            contact.setPhoneNumber(contactDto.getPhoneNumber());
            contact.setDescription(contactDto.getDescription());
            
            contactRepository.save(contact);

            javaMailSender.send(message);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("We have received your concern. Our team will reach out to you soon!");

        } catch (Exception e) {
            System.err.println("Error sending mail: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while submitting your concern.");
        }
    }
}
