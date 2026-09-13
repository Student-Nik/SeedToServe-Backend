package com.seedtoserve.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.model.Admin;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.DeliveryBoy;
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

	private final PasswordEncoder passwordEncoder;

	private final ForgotPasswordEmailService forgotPasswordEmailService;

	// Store OTP temporarily
	private final Map<String, String> otpStore = new HashMap<>();

	// Generate OTP
	private String generateOtp() {

		SecureRandom random = new SecureRandom();

		int otp = 100000 + random.nextInt(900000);

		return String.valueOf(otp);
	}

	// Send OTP
	private void sendOtp(String email) {

		String otp = generateOtp();

		// Store OTP against email
		otpStore.put(email, otp);

		System.out.println("Password Reset OTP: " + otp);

		forgotPasswordEmailService.sendOtpEmail(email, otp);
	}

	// Forgot Password
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

	// Verify OTP
	private boolean verifyOtp(String email, String otp) {

		String storedOtp = otpStore.get(email);

		if (storedOtp == null) {
			return false;
		}

		return storedOtp.equals(otp);
	}

	// Reset Password
	public boolean resetPassword(String email, String otp, String newPassword) {

		// Verify OTP
		if (!verifyOtp(email, otp)) {
			return false;
		}

		// Customer - BUYER / FARMER
		Optional<Customer> customer = customerRepository.findByEmail(email);

		if (customer.isPresent()) {

			customer.get().setPassword(passwordEncoder.encode(newPassword));

			customerRepository.save(customer.get());

			otpStore.remove(email);

			return true;
		}

		// Delivery Boy
		Optional<DeliveryBoy> deliveryBoy = deliveryBoyRepository.findByEmail(email);

		if (deliveryBoy.isPresent()) {

			deliveryBoy.get().setPassword(passwordEncoder.encode(newPassword));

			deliveryBoyRepository.save(deliveryBoy.get());

			otpStore.remove(email);

			return true;
		}

		// Admin
		Optional<Admin> admin = adminRepository.findByEmail(email);

		if (admin.isPresent()) {

			admin.get().setPassword(passwordEncoder.encode(newPassword));

			adminRepository.save(admin.get());

			otpStore.remove(email);

			return true;
		}

		return false;
	}
}