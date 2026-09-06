package com.seedtoserve.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.AllRolesLoginRequest;
import com.seedtoserve.dto.AllRolesLoginResponse;
import com.seedtoserve.service.AllRolesLoginService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AllRolesLoginController {

	private final AllRolesLoginService allRolesLoginService;
	
	private AllRolesLoginController(AllRolesLoginService allRolesLoginService) {
		super();
		this.allRolesLoginService = allRolesLoginService;
	}

	// FARMER/BUYER, ADMIN, DELIVERY BOY LOGIN 
	@PostMapping("/login")
	public ResponseEntity<AllRolesLoginResponse> login(@Valid @RequestBody AllRolesLoginRequest request) {
		return allRolesLoginService.login(request);
	}

}
