package com.seedtoserve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.security.JwtUtil;

@RestController
public class SecurityTestController {

	@Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/generate-token")
    public String generateToken() {
        String token = jwtUtil.createToken("nkute611@gmail.com"); 
        return token;
    }
}
