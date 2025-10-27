package com.seedtoserve.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer")
public class FarmerController {

	@GetMapping("/farmer-dashboard")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<String> farmerDashboard() {
        return ResponseEntity.ok("Welcome, Farmer!");
    }
}
