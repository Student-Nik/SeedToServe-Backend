package com.seedtoserve.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryBoyEmailService {

	private final JavaMailSender mailSender;

	public void sendDeliveryBoyCredentials(String email, String firstName, String temporaryPassword) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(email);
		message.setSubject("Welcome to SeedToServe - Delivery Boy Account");

		message.setText("Hello " + firstName + ",\n\n" + "Congratulations!\n\n"
				+ "You have been registered as a Delivery Boy for SeedToServe.\n\n"
				+ "You can now log in to your Delivery Boy account using the credentials below:\n\n" + "Email: " + email
				+ "\n" + "Temporary Password: " + temporaryPassword + "\n\n"
				+ "Please change your password after your first login.\n\n" + "Thank you,\n" + "SeedToServe Team");

		mailSender.send(message);
	}
}
