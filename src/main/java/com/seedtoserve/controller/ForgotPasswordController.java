package com.seedtoserve.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.ForgotPasswordRequest;
import com.seedtoserve.service.ForgotPasswordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ForgotPasswordController {

	private final ForgotPasswordService forgotPasswordService;

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		boolean sent = forgotPasswordService.forgotPassword(request.getEmail());

		if (sent) {
			return ResponseEntity.ok(Map.of("message", "OTP sent successfully to your email"));
		}

		return ResponseEntity.ok(Map.of("message", "Email not found"));
	}
}
