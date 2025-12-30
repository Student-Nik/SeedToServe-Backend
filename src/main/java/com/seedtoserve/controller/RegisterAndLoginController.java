package com.seedtoserve.controller;

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

import com.seedtoserve.dto.ApiResponse;
import com.seedtoserve.dto.CustomerDTO;
import com.seedtoserve.dto.JwtLoginResponse;
import com.seedtoserve.dto.LoginRequest;
import com.seedtoserve.model.Customer;
import com.seedtoserve.security.CustomerUserDetails;
import com.seedtoserve.security.JwtUtil;
import com.seedtoserve.service.CustomerService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class RegisterAndLoginController {

    @Autowired
    private CustomerService customerService;

    // Registration
    @PostMapping("/api/auth/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody CustomerDTO customerDto) {
        return customerService.registerUser(customerDto);
    }

    // Login
    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse<JwtLoginResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        return customerService.loginUser(loginRequest);
    }

}
