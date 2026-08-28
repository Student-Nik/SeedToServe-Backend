package com.seedtoserve.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.DeliveryBoyRequest;
import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.service.DeliveryBoyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/delivery/boy")
@RequiredArgsConstructor
public class DeliveryBoyController {

	private final DeliveryBoyService deliveryBoyService;

	@PostMapping
	public ResponseEntity<?> createDeliveryBoy(@Valid @RequestBody DeliveryBoyRequest dto) {

		try {

			DeliveryBoy deliveryBoy = deliveryBoyService.createDeliveryBoy(dto);

			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Delivery Boy created successfully",
					"deliveryBoyId", deliveryBoy.getId(), "email", deliveryBoy.getEmail()));

		} catch (RuntimeException e) {

			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}
}
