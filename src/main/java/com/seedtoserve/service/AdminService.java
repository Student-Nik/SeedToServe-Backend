package com.seedtoserve.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.AdminLoginRequest;
import com.seedtoserve.dto.AdminLoginResponse;
import com.seedtoserve.model.Admin;
import com.seedtoserve.repository.AdminRepository;
import com.seedtoserve.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	// Admin Log in
	public AdminLoginResponse login(AdminLoginRequest request) {

		Admin admin = adminRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		if (!"ACTIVE".equalsIgnoreCase(admin.getStatus())) {
			throw new RuntimeException("Admin account is inactive");
		}

		if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {

			throw new RuntimeException("Invalid email or password");
		}

		String token = jwtUtil.createToken(admin.getEmail());

		return new AdminLoginResponse(token, admin.getId(), admin.getEmail(), "ADMIN");
	}

}
