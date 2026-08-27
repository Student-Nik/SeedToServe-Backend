package com.seedtoserve.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.AdminDashboardResponse;
import com.seedtoserve.dto.AdminLoginRequest;
import com.seedtoserve.dto.AdminLoginResponse;
import com.seedtoserve.dto.AdminOrderResponse;
import com.seedtoserve.dto.AdminProfileResponse;
import com.seedtoserve.enums.OrderStatus;
import com.seedtoserve.model.Admin;
import com.seedtoserve.model.Order;
import com.seedtoserve.repository.AdminRepository;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.repository.OrderRepository;
import com.seedtoserve.repository.ProductRepository;
import com.seedtoserve.security.JwtUtil;

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

}
