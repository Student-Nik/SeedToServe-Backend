package com.seedtoserve.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.CustomerDTO;
import com.seedtoserve.dto.JwtLoginResponse;
import com.seedtoserve.dto.LoginRequest;
import com.seedtoserve.dto.RegisterAndSendEmailRequestDTO;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Mail;
import com.seedtoserve.security.CustomerUserDetails;
import com.seedtoserve.security.JwtUtil;
import com.seedtoserve.service.CustomerService;
import com.seedtoserve.service.MailService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class RegisterAndLoginController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // Registration
    @PostMapping("/api/auth/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody CustomerDTO customerDto) {

        return customerService.registerUser(customerDto);
    }


    // Login
    @PostMapping("/api/auth/login")
    public ResponseEntity<JwtLoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Get authenticated user details
            CustomerUserDetails userDetails = (CustomerUserDetails) auth.getPrincipal();
            Customer customer = userDetails.getCustomer();

            // Generate JWT token
            String token = jwtUtil.createToken(userDetails.getUsername());

            // Build JWT response
            JwtLoginResponse response = new JwtLoginResponse();
            response.setToken(token);
            response.setUsername(customer.getEmail());
            response.setRole(customer.getRegistrationType().toUpperCase());

            //  Return JSON with the token
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
