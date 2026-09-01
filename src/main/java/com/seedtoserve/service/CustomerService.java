package com.seedtoserve.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.ApiResponse;
import com.seedtoserve.dto.CustomerDTO;
import com.seedtoserve.dto.JwtLoginResponse;
import com.seedtoserve.dto.LoginRequest;
import com.seedtoserve.model.Customer;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.security.CustomerUserDetails;
import com.seedtoserve.security.JwtUtil;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	@Qualifier("customerAuthenticationManager")
	private AuthenticationManager authenticationManager;

	@Autowired
	private MailService mailService;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// =====================================================
	// REGISTER
	// =====================================================

	public ResponseEntity<Map<String, Object>> registerUser(CustomerDTO customerDto) {

		// Check duplicate email
		if (customerRepository.findByEmail(customerDto.getEmail()).isPresent()) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "This E-mail already exists, please try another one!"));
		}

		// Check password match
		if (!customerDto.getPassword().equals(customerDto.getConfirmPassword())) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Password and Confirm Password do not match!"));
		}

		// Map DTO to Entity
		Customer customer = new Customer();

		customer.setRegistrationType(customerDto.getRegistrationType());

		customer.setFirstName(customerDto.getFirstName());

		customer.setLastName(customerDto.getLastName());

		customer.setEmail(customerDto.getEmail());

		customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));

		// Save customer
		customerRepository.save(customer);

		// Full name
		String fullName = customer.getFirstName() + " " + customer.getLastName();

		// Send email
		mailService.sendAndLogEmail(customer.getEmail(), fullName, customer.getRegistrationType());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("message", "Customer registration successful and email sent!", "registrationType",
						customer.getRegistrationType()));
	}

	// =====================================================
	// LOGIN
	// =====================================================

	public ResponseEntity<ApiResponse<JwtLoginResponse>> loginUser(LoginRequest loginRequest) {

		try {

			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			CustomerUserDetails userDetails = (CustomerUserDetails) auth.getPrincipal();

			Customer customer = userDetails.getCustomer();

			String role = customer.getRegistrationType().toUpperCase();

			String token = jwtUtil.createToken(userDetails.getUsername(), role);

			JwtLoginResponse jwtResponse = new JwtLoginResponse();

			jwtResponse.setToken(token);
			jwtResponse.setUsername(customer.getEmail());
			jwtResponse.setRole(role);

			return ResponseEntity.ok(new ApiResponse<>(true, "Login successful!", jwtResponse));

		} catch (BadCredentialsException e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>(false, "Invalid Credentials!", null));
		}
	}

	// =====================================================
	// GET LOGGED-IN CUSTOMER
	// =====================================================

	public Customer getLoggedInCustomer() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		return customerRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Customer not found for email: " + email));
	}
}