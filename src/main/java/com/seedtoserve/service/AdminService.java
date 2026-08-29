package com.seedtoserve.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.AdminDashboardResponse;
import com.seedtoserve.dto.AdminLoginRequest;
import com.seedtoserve.dto.AdminLoginResponse;
import com.seedtoserve.dto.AdminOrderDetailsResponse;
import com.seedtoserve.dto.AdminOrderItemResponse;
import com.seedtoserve.dto.AdminOrderResponse;
import com.seedtoserve.dto.AdminProfileResponse;
import com.seedtoserve.dto.BuyerAddressForAdminResponse;
import com.seedtoserve.enums.OrderStatus;
import com.seedtoserve.model.AddressDetails;
import com.seedtoserve.model.Admin;
import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.model.Order;
import com.seedtoserve.repository.AdminRepository;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.repository.DeliveryBoyRepository;
import com.seedtoserve.repository.OrderRepository;
import com.seedtoserve.repository.ProductRepository;
import com.seedtoserve.security.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final DeliveryBoyRepository deliveryBoyRepository;
	private final DeliveryBoyEmailService deliveryBoyEmailService;

	// Admin Log in
	public AdminLoginResponse login(AdminLoginRequest request) {

		Admin admin = adminRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		if (!"ACTIVE".equalsIgnoreCase(admin.getStatus())) {
			throw new RuntimeException("Admin account is inactive");
		}

		if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {

			throw new RuntimeException("Invalid email or password");
		}

		String token = jwtUtil.createAdminToken(admin.getEmail(), "ADMIN");

		return new AdminLoginResponse(token, admin.getId(), admin.getEmail(), "ADMIN");
	}

	// Admin Profile
	public AdminProfileResponse getProfile(String email) {
		Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Admin not found"));
		return new AdminProfileResponse(admin.getId(), admin.getEmail(), admin.getStatus());
	}

	// Admin Dashboard
	public AdminDashboardResponse getDashboard() {

		long totalBuyers = customerRepository.countByRegistrationType("BUYER");

		long totalFarmers = customerRepository.countByRegistrationType("FARMER");

		long totalProducts = productRepository.count();

		long totalOrders = orderRepository.count();

		long pendingOrders = orderRepository.countByOrderStatus(OrderStatus.PENDING);

		long completedOrders = orderRepository.countByOrderStatus(OrderStatus.DELIVERED);

		long cancelledOrders = orderRepository.countByOrderStatus(OrderStatus.CANCELLED);

		Double revenue = orderRepository.getTotalRevenueByStatus(OrderStatus.DELIVERED);

		BigDecimal totalRevenue = BigDecimal.valueOf(revenue != null ? revenue : 0.0);

		return new AdminDashboardResponse(totalBuyers, totalFarmers, totalProducts, totalOrders, pendingOrders,
				completedOrders, cancelledOrders, totalRevenue);
	}

	// Show orders placed by buyers to admin
	public List<AdminOrderResponse> getAllOrdersForAdmin() {

		List<Order> orders = orderRepository.findAll();

		return orders.stream()
				.map(order -> new AdminOrderResponse(order.getId(), order.getCustomer().getFirstName(),
						order.getTotalAmount(), order.getPaymentMethod(), order.getOrderStatus(),
						order.getPaymentStatus(), order.getOrderDate()))
				.toList();
	}

	// Admin gets a particular order
	public AdminOrderDetailsResponse getOrderDetailsForAdmin(int orderId) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

		List<AdminOrderItemResponse> items = order.getOrderItems().stream()
				.map(item -> new AdminOrderItemResponse(item.getProduct().getName(), item.getProduct().getImage(),
						item.getQuantity(), item.getPrice()))
				.toList();

		AddressDetails address = order.getAddressDetails();

		BuyerAddressForAdminResponse addressResponse = new BuyerAddressForAdminResponse(address.getFullName(),
				address.getMobileNo(), address.getHouseNoOrStreet(), address.getVillageOrTown(), address.getDistrict(),
				address.getState(), address.getPincode());

		return new AdminOrderDetailsResponse(order.getId(), order.getCustomer().getFirstName(), order.getTotalAmount(),
				order.getPaymentMethod(), order.getPaymentStatus(), order.getOrderStatus(), order.getOrderDate(),
				addressResponse, items);
	}

	// Assign Delivery boy
	@Transactional
	public ResponseEntity<?> assignDeliveryBoy(int orderId, int deliveryBoyId) {

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() ->
	                    new RuntimeException("Order not found"));

	    DeliveryBoy deliveryBoy = deliveryBoyRepository.findById(deliveryBoyId)
	            .orElseThrow(() ->
	                    new RuntimeException("Delivery boy not found"));

	    // Check availability
	    if (!deliveryBoy.isAvailable()) {
	        throw new RuntimeException(
	                "Delivery boy is currently not available");
	    }

	    // Check whether order is already assigned
	    if (order.getDeliveryBoy() != null) {
	        throw new RuntimeException(
	                "A delivery boy is already assigned to this order");
	    }

	    // Assign delivery boy
	    order.setDeliveryBoy(deliveryBoy);

	    // Update order status
	    order.setOrderStatus(OrderStatus.ASSIGNED);

	    // Delivery boy is now busy
	    deliveryBoy.setAvailable(false);

	    // Save changes
	    orderRepository.save(order);
	    deliveryBoyRepository.save(deliveryBoy);

	    // Send email after successful database update
	    deliveryBoyEmailService.sendOrderAssignmentEmail(
	            deliveryBoy,
	            order
	    );

	    return ResponseEntity.ok(
	            Map.of(
	                    "message", "Delivery boy assigned successfully",
	                    "orderId", order.getId(),
	                    "deliveryBoyId", deliveryBoy.getId(),
	                    "orderStatus", order.getOrderStatus()
	            )
	    );
	}

}
