package com.seedtoserve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.model.Customer;
import com.seedtoserve.repository.CustomerRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private CustomerRepository customerRepository;
	
	@GetMapping("/me")
    public Customer getLoggedInUser(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return null;
        }

        return customerRepository.findByEmail(userDetails.getUsername())
                .orElseThrow( () -> new RuntimeException("Customer not found!"));
    }
}
