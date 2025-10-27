package com.seedtoserve.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buyer")
public class BuyerController {

	@GetMapping("/buyer-dashboard")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<String> buyerOrders() {
        return ResponseEntity.ok("Welcome, Buyer!");
    }
}
