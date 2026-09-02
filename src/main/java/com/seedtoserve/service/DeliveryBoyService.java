package com.seedtoserve.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.DeliveryBoyForAdminResponse;
import com.seedtoserve.dto.DeliveryBoyLoginRequest;
import com.seedtoserve.dto.DeliveryBoyLoginResponse;
import com.seedtoserve.dto.DeliveryBoyOrderItemResponse;
import com.seedtoserve.dto.DeliveryBoyOrderResponse;
import com.seedtoserve.dto.DeliveryBoyRequest;
import com.seedtoserve.dto.UpdateOrderStatusRequest;
import com.seedtoserve.enums.OrderStatus;
import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.model.Order;
import com.seedtoserve.repository.DeliveryBoyRepository;
import com.seedtoserve.repository.OrderRepository;
import com.seedtoserve.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class DeliveryBoyService {

	private final DeliveryBoyRepository deliveryBoyRepository;

	private final PasswordEncoder passwordEncoder;

	private final DeliveryBoyEmailService deliveryBoyEmailService;

	private final JwtUtil jwtUtil;
	
	private final BuyerOrderEmailService buyerOrderEmailService;

	private final OrderRepository orderRepository;
	private final AuthenticationManager deliveryBoyAuthenticationManager;


	public DeliveryBoyService(
	        DeliveryBoyRepository deliveryBoyRepository,
	        PasswordEncoder passwordEncoder,
	        DeliveryBoyEmailService deliveryBoyEmailService,
	        JwtUtil jwtUtil,
	        BuyerOrderEmailService buyerOrderEmailService,
	        OrderRepository orderRepository,
	        @Qualifier("deliveryBoyAuthenticationManager")
	        AuthenticationManager deliveryBoyAuthenticationManager) {

	    super();

	    this.deliveryBoyRepository = deliveryBoyRepository;
	    this.passwordEncoder = passwordEncoder;
	    this.deliveryBoyEmailService = deliveryBoyEmailService;
	    this.jwtUtil = jwtUtil;
	    this.buyerOrderEmailService = buyerOrderEmailService;
	    this.orderRepository = orderRepository;
	    this.deliveryBoyAuthenticationManager =
	            deliveryBoyAuthenticationManager;
	}

	// =====================================================
	// CREATE DELIVERY BOY
	// =====================================================

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

		// Send credentials by email
		deliveryBoyEmailService.sendDeliveryBoyCredentials(deliveryBoyRequest.getEmail(),
				deliveryBoyRequest.getFirstName(), temporaryPassword);

		return savedDeliveryBoy;
	}

	// =====================================================
	// GENERATE TEMPORARY PASSWORD
	// =====================================================

	private String generateTemporaryPassword() {

		return UUID.randomUUID().toString().substring(0, 8);
	}

	// =====================================================
	// DELIVERY BOY LOGIN
	// =====================================================

	public ResponseEntity<?> login(DeliveryBoyLoginRequest request) {

		try {

			Authentication authentication = deliveryBoyAuthenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			/*
			 * Authentication successful.
			 *
			 * DeliveryBoyUserAuthenticationService has already:
			 *
			 * 1. Found the delivery boy 2. Loaded the encoded password 3. Checked the
			 * password 4. Assigned ROLE_DELIVERY_BOY
			 */

			String email = authentication.getName();

			DeliveryBoy deliveryBoy = deliveryBoyRepository.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("Delivery boy not found"));

			// Create JWT
			String token = jwtUtil.createToken(deliveryBoy.getEmail(), "DELIVERY_BOY");

			DeliveryBoyLoginResponse response = new DeliveryBoyLoginResponse("Delivery Boy Login Successful", token,
					deliveryBoy.getId(), deliveryBoy.getFirstName(), deliveryBoy.getLastName(), deliveryBoy.getEmail());

			return ResponseEntity.ok(response);

		} catch (BadCredentialsException e) {

			throw new RuntimeException("Invalid email or password");
		}
	}

	// =====================================================
	// SHOW ALL DELIVERY BOYS
	// =====================================================

	public List<DeliveryBoyForAdminResponse> getAllDeliveryBoys() {

		return deliveryBoyRepository.findAll().stream()
				.map(deliveryBoy -> new DeliveryBoyForAdminResponse(deliveryBoy.getId(), deliveryBoy.getFirstName(),
						deliveryBoy.getLastName(), deliveryBoy.getMobileNo(), deliveryBoy.getEmail(),
						deliveryBoy.isAvailable()))
				.toList();
	}

	// =====================================================
	// GET ASSIGNED ORDERS BY DELIVERY BOY EMAIL
	// =====================================================

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

	// =====================================================
	// Update the order status
	// =====================================================

	@Transactional
	public ResponseEntity<?> updateOrderStatus(int orderId, UpdateOrderStatusRequest request,
			Authentication authentication) {

		// Get logged-in delivery boy email/username from JWT
		String email = authentication.getName();

		DeliveryBoy deliveryBoy = deliveryBoyRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Delivery boy not found"));

		// Find order
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

		// Check whether this order belongs to logged-in delivery boy
		if (order.getDeliveryBoy() == null || order.getDeliveryBoy().getId() != deliveryBoy.getId()) {

			throw new RuntimeException("This order is not assigned to you");
		}

		OrderStatus currentStatus = order.getOrderStatus();
		OrderStatus newStatus = request.getOrderStatus();

		// ASSIGNED -> SHIPPED
		if (currentStatus == OrderStatus.ASSIGNED && newStatus == OrderStatus.SHIPPED) {

			order.setOrderStatus(OrderStatus.SHIPPED);
		}

		// SHIPPED -> OUT_FOR_DELIVERY
		else if (currentStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.OUT_FOR_DELIVERY) {

			order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
		}

		// OUT_FOR_DELIVERY -> DELIVERED
		else if (currentStatus == OrderStatus.OUT_FOR_DELIVERY && newStatus == OrderStatus.DELIVERED) {

			order.setOrderStatus(OrderStatus.DELIVERED);

			// Delivery boy is available for another order
			deliveryBoy.setAvailable(true);
			deliveryBoyRepository.save(deliveryBoy);
		}

		else {
			throw new RuntimeException("Invalid order status transition: " + currentStatus + " -> " + newStatus);
		}

		orderRepository.save(order);

		buyerOrderEmailService.sendOrderStatusEmail(order);

		return ResponseEntity.ok(Map.of("message", "Order status updated successfully", "orderId", order.getId(),
				"orderStatus", order.getOrderStatus()));
	}
}