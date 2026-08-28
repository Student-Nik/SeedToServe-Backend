package com.seedtoserve.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.DeliveryBoyRequest;
import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.repository.DeliveryBoyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryBoyService {

	private final DeliveryBoyRepository deliveryBoyRepository;
	private final PasswordEncoder passwordEncoder;
	private final DeliveryBoyEmailService deliveryBoyEmailService;
	
	public DeliveryBoy createDeliveryBoy(DeliveryBoyRequest deliveryBoyRequest) {

        if (deliveryBoyRepository.existsByEmail(deliveryBoyRequest.getEmail())) {
            throw new RuntimeException("Delivery Boy with this email already exists");
        }

        // Generate temporary password
        String temporaryPassword = generateTemporaryPassword();

        DeliveryBoy deliveryBoy = DeliveryBoy.builder()
                .firstName(deliveryBoyRequest.getFirstName())
                .lastName(deliveryBoyRequest.getLastName())
                .mobileNo(deliveryBoyRequest.getMobileNo())
                .email(deliveryBoyRequest.getEmail())
                .password(passwordEncoder.encode(temporaryPassword))
                .available(true)
                .build();

        DeliveryBoy savedDeliveryBoy =
                deliveryBoyRepository.save(deliveryBoy);

        deliveryBoyEmailService.sendDeliveryBoyCredentials(deliveryBoyRequest.getEmail(), deliveryBoyRequest.getFirstName(), temporaryPassword);

        return savedDeliveryBoy;
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 8);
    }
}
