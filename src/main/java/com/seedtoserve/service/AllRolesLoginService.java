package com.seedtoserve.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.AllRolesLoginRequest;
import com.seedtoserve.dto.AllRolesLoginResponse;
import com.seedtoserve.model.Admin;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.repository.AdminRepository;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.repository.DeliveryBoyRepository;
import com.seedtoserve.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
public class AllRolesLoginService {

	private final AdminRepository adminRepository;
	private final CustomerRepository customerRepository;
	private final DeliveryBoyRepository deliveryBoyRepository;

	private final JwtUtil jwtUtil;

	private final AuthenticationManager authenticationManager;
	private final AuthenticationManager adminAuthenticationManager;
	private final AuthenticationManager deliveryBoyAuthenticationManager;

	public AllRolesLoginService(
	        AdminRepository adminRepository,
	        CustomerRepository customerRepository,
	        DeliveryBoyRepository deliveryBoyRepository,
	        JwtUtil jwtUtil,
	        @Qualifier("customerAuthenticationManager")
	        AuthenticationManager authenticationManager,
	        @Qualifier("adminAuthenticationManager")
	        AuthenticationManager adminAuthenticationManager,
	        @Qualifier("deliveryBoyAuthenticationManager")
	        AuthenticationManager deliveryBoyAuthenticationManager) {

	    this.adminRepository = adminRepository;
	    this.customerRepository = customerRepository;
	    this.deliveryBoyRepository = deliveryBoyRepository;
	    this.jwtUtil = jwtUtil;
	    this.authenticationManager = authenticationManager;
	    this.adminAuthenticationManager = adminAuthenticationManager;
	    this.deliveryBoyAuthenticationManager = deliveryBoyAuthenticationManager;
	}

	public ResponseEntity<AllRolesLoginResponse> login(AllRolesLoginRequest request) {

		String email = request.getEmail().trim().toLowerCase();
		String password = request.getPassword();
		
		// Testing
		System.out.println("EMAIL = " + email);
		System.out.println("ADMIN EXISTS = "
		        + adminRepository.findByEmail(email).isPresent());

		System.out.println("DELIVERY EXISTS = "
		        + deliveryBoyRepository.findByEmail(email).isPresent());

		System.out.println("CUSTOMER EXISTS = "
		        + customerRepository.findByEmail(email).isPresent());

		try {

			// ================= ADMIN =================
			if (adminRepository.findByEmail(email).isPresent()) {

				Authentication authentication = adminAuthenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(email, password));

				Admin admin = adminRepository.findByEmail(email)
						.orElseThrow(() -> new RuntimeException("Admin not found"));

				if (!"ACTIVE".equalsIgnoreCase(admin.getStatus())) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
							new AllRolesLoginResponse(false, "Admin account is inactive", null, null, null, "ADMIN"));
				}

				String token = jwtUtil.createToken(admin.getEmail(), "ADMIN");

				return ResponseEntity.ok(new AllRolesLoginResponse(true, "Admin login successful", token, admin.getId(),
						admin.getEmail(), "ADMIN"));
			}

			// ================= DELIVERY BOY =================
			if (deliveryBoyRepository.findByEmail(email).isPresent()) {

				Authentication authentication = deliveryBoyAuthenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(email, password));

				String authenticatedEmail = authentication.getName();

				DeliveryBoy deliveryBoy = deliveryBoyRepository.findByEmail(authenticatedEmail)
						.orElseThrow(() -> new RuntimeException("Delivery boy not found"));

				String token = jwtUtil.createToken(deliveryBoy.getEmail(), "DELIVERY_BOY");

				return ResponseEntity.ok(new AllRolesLoginResponse(true, "Delivery Boy login successful", token,
						(long) deliveryBoy.getId(), deliveryBoy.getEmail(), "DELIVERY_BOY"));
			}

			// ================= FARMER / BUYER =================
			if (customerRepository.findByEmail(email).isPresent()) {

				Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(email, password));

				Customer customer = customerRepository.findByEmail(email)
						.orElseThrow(() -> new RuntimeException("Customer not found"));

				String role = customer.getRegistrationType().toUpperCase();

				String token = jwtUtil.createToken(customer.getEmail(), role);

				return ResponseEntity.ok(new AllRolesLoginResponse(true, "Login successful", token, customer.getId(),
						customer.getEmail(), role));
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AllRolesLoginResponse(false, "Invalid email or password", null, null, null, null));

		} catch (BadCredentialsException e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AllRolesLoginResponse(false, "Invalid email or password", null, null, null, null));
		}
	}

}
