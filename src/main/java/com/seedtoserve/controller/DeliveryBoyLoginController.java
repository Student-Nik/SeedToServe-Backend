package com.seedtoserve.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.DeliveryBoyLoginRequest;
import com.seedtoserve.service.DeliveryBoyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/delivery/boy")
@RequiredArgsConstructor
public class DeliveryBoyLoginController {

	private final DeliveryBoyService deliveryBoyService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody DeliveryBoyLoginRequest request) {
        return deliveryBoyService.login(request);
    }
}
