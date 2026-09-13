package com.seedtoserve.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import com.seedtoserve.repository.AdminRepository;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.repository.DeliveryBoyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final CustomerRepository customerRepository;
    private final DeliveryBoyRepository deliveryBoyRepository;
    private final AdminRepository adminRepository;

    private final ForgotPasswordEmailService forgotPasswordEmailService;

    // Helper Method
    private String generateOtp() {

        SecureRandom random = new SecureRandom();

        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }

    // Send OTP
    private void sendOtp(String email) {
        String otp = generateOtp();
        System.out.println("Password Reset OTP: " + otp);
        forgotPasswordEmailService.sendOtpEmail(email, otp);
    }

    public boolean forgotPassword(String email) {

        // Customer - BUYER / FARMER
        if (customerRepository.findByEmail(email).isPresent()) {
            sendOtp(email);
            return true;
        }

        // Delivery Boy
        if (deliveryBoyRepository.findByEmail(email).isPresent()) {
            sendOtp(email);
            return true;
        }

        // Admin
        if (adminRepository.findByEmail(email).isPresent()) {
            sendOtp(email);
            return true;
        }

        return false;
    }
}