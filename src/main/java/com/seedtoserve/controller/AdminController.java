package com.seedtoserve.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.AdminLoginRequest;
import com.seedtoserve.dto.AdminLoginResponse;
import com.seedtoserve.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

	private final AdminService adminService;

	// Admin Log in
	@PostMapping("/login")
	public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
		return ResponseEntity.ok(adminService.login(request));
	}
}
