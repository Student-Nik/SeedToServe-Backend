package com.seedtoserve.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.CustomerDTO;
import com.seedtoserve.dto.LoginRequest;
import com.seedtoserve.dto.JwtLoginResponse;
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
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private MailService mailService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register
    public ResponseEntity<String> registerUser(CustomerDTO customerDto) {

        Optional<Customer> existingUser = customerRepository.findByEmail(customerDto.getEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("This E-mail already exists, please try another one!");
        }

        if (!customerDto.getPassword().equals(customerDto.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Password and Confirm Password do not match!");
        }

        Customer customer = new Customer();
        customer.setRegistrationType(customerDto.getRegistrationType());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));

        
        customerRepository.save(customer);
        

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Customer registration successful");
    }

    // Login
    public ResponseEntity<JwtLoginResponse> loginUser(LoginRequest loginRequest) {
        try {
            // Authenticate using AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Fetch user details
            Customer customer = customerRepository.findByEmail(loginRequest.getEmail()).get();
            CustomerUserDetails userDetails = new CustomerUserDetails(customer);

            // Generate JWT
            String token = jwtUtil.createToken(userDetails.getUsername());

            // Prepare JWT response
            JwtLoginResponse response = new JwtLoginResponse();
            response.setToken(token);
            response.setUsername(customer.getEmail());
            response.setRole(customer.getRegistrationType().toUpperCase());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    

    public Customer getLoggedInCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();  // Extracted from JWT
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found for email: " + email));
    }
}
