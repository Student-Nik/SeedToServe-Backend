package com.seedtoserve.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.AdminDashboardResponse;
import com.seedtoserve.dto.AdminLoginRequest;
import com.seedtoserve.dto.AdminLoginResponse;
import com.seedtoserve.dto.AdminOrderDetailsResponse;
import com.seedtoserve.dto.AdminOrderResponse;
import com.seedtoserve.dto.AdminProfileResponse;
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

	// Admin Profile
	@GetMapping("/profile")
	public ResponseEntity<AdminProfileResponse> getProfile(Authentication authentication) {
		return ResponseEntity.ok(adminService.getProfile(authentication.getName()));
	}

	// Admin Dashboard
	@GetMapping("/dashboard")
	public ResponseEntity<AdminDashboardResponse> getDashboard() {
		return ResponseEntity.ok(adminService.getDashboard());
	}

	// Admin can see all orders placed by buyers.
	@GetMapping("/orders")
	public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
		return ResponseEntity.ok(adminService.getAllOrdersForAdmin());
	}

	// Admin gets a particular order
	@GetMapping("/orders/{orderId}")
	public ResponseEntity<AdminOrderDetailsResponse> getOrderDetails(@PathVariable int orderId) {
		AdminOrderDetailsResponse response = adminService.getOrderDetailsForAdmin(orderId);
		return ResponseEntity.ok(response);
	}

	// Assign delivery boy to order
	@PutMapping("/orders/{orderId}/assign-delivery-boy/{deliveryBoyId}")
	public ResponseEntity<?> assignDeliveryBoy(@PathVariable int orderId, @PathVariable int deliveryBoyId) {
		try {
			return adminService.assignDeliveryBoy(orderId, deliveryBoyId);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}
}
