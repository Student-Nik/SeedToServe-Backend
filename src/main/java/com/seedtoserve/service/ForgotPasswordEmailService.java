package com.seedtoserve.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgotPasswordEmailService {

	private final JavaMailSender javaMailSender;
	
	public void sendOtpEmail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("SeedToServe - Password Reset OTP");

        message.setText(
                "Hello,\n\n" +
                "Your SeedToServe password reset OTP is:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Regards,\n" +
                "SeedToServe Team"
        );

        javaMailSender.send(message);
    }
}
