package com.seedtoserve.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.DeliveryBoyForAdminResponse;
import com.seedtoserve.dto.DeliveryBoyLoginRequest;
import com.seedtoserve.dto.DeliveryBoyLoginResponse;
import com.seedtoserve.dto.DeliveryBoyOrderItemResponse;
import com.seedtoserve.dto.DeliveryBoyOrderResponse;
import com.seedtoserve.dto.DeliveryBoyRequest;
import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.model.Order;
import com.seedtoserve.repository.DeliveryBoyRepository;
import com.seedtoserve.repository.OrderRepository;
import com.seedtoserve.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryBoyService {

	private final DeliveryBoyRepository deliveryBoyRepository;
	private final PasswordEncoder passwordEncoder;
	private final DeliveryBoyEmailService deliveryBoyEmailService;
	private final JwtUtil jwtUtil;
	private final OrderRepository orderRepository;

	// Create Delivery Boy
	public DeliveryBoy createDeliveryBoy(DeliveryBoyRequest deliveryBoyRequest) {

		if (deliveryBoyRepository.existsByEmail(deliveryBoyRequest.getEmail())) {
			throw new RuntimeException("Delivery Boy with this email already exists");
		}

		// Generate temporary password
		String temporaryPassword = generateTemporaryPassword();

		DeliveryBoy deliveryBoy = DeliveryBoy.builder().firstName(deliveryBoyRequest.getFirstName())
				.lastName(deliveryBoyRequest.getLastName()).mobileNo(deliveryBoyRequest.getMobileNo())
				.email(deliveryBoyRequest.getEmail()).password(passwordEncoder.encode(temporaryPassword))
				.available(true).build();

		DeliveryBoy savedDeliveryBoy = deliveryBoyRepository.save(deliveryBoy);

		deliveryBoyEmailService.sendDeliveryBoyCredentials(deliveryBoyRequest.getEmail(),
				deliveryBoyRequest.getFirstName(), temporaryPassword);

		return savedDeliveryBoy;
	}

	private String generateTemporaryPassword() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	// Delivery Boy Login
	public ResponseEntity<?> login(DeliveryBoyLoginRequest request) {

		DeliveryBoy deliveryBoy = deliveryBoyRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), deliveryBoy.getPassword())) {

			throw new RuntimeException("Invalid email or password");
		}

		String token = jwtUtil.createAdminToken(deliveryBoy.getEmail(), "DELIVERY_BOY");

		DeliveryBoyLoginResponse response = new DeliveryBoyLoginResponse("Delivery Boy Login Successful", token,
				deliveryBoy.getId(), deliveryBoy.getFirstName(), deliveryBoy.getLastName(), deliveryBoy.getEmail());

		return ResponseEntity.ok(response);
	}

	// Show all delivery boy's
	public List<DeliveryBoyForAdminResponse> getAllDeliveryBoys() {

		return deliveryBoyRepository.findAll().stream()
				.map(deliveryBoy -> new DeliveryBoyForAdminResponse(deliveryBoy.getId(), deliveryBoy.getFirstName(),
						deliveryBoy.getLastName(), deliveryBoy.getMobileNo(), deliveryBoy.getEmail(),
						deliveryBoy.isAvailable()))
				.toList();
	}

	//
	public List<DeliveryBoyOrderResponse> getAssignedOrdersByEmail(String email) {

		DeliveryBoy deliveryBoy = deliveryBoyRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Delivery boy not found with email: " + email));

		List<Order> orders = orderRepository.findByDeliveryBoyId(deliveryBoy.getId());

		return orders.stream().map(order -> {

			String customerName = order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName();

			String address = order.getAddressDetails().getHouseNoOrStreet() + ", "
					+ order.getAddressDetails().getVillageOrTown() + ", " + order.getAddressDetails().getDistrict()
					+ ", " + order.getAddressDetails().getState() + " - " + order.getAddressDetails().getPincode();

			List<DeliveryBoyOrderItemResponse> items = order.getOrderItems().stream()
					.map(item -> new DeliveryBoyOrderItemResponse(item.getProduct().getName(),
							item.getProduct().getImage(), item.getQuantity(), item.getPrice()))
					.toList();

			return new DeliveryBoyOrderResponse(order.getId(), customerName, order.getTotalAmount(),
					order.getPaymentMethod().toString(), order.getPaymentStatus(), order.getOrderStatus(),
					order.getOrderDate(), address, items);

		}).toList();
	}
}
