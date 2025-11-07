package com.seedtoserve.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.seedtoserve.config.RazorpayConfig;
import com.seedtoserve.dto.CreatePaymentResponseDto;
import com.seedtoserve.dto.VerifyPaymentRequestDto;
import com.seedtoserve.model.Order;
import com.seedtoserve.security.CustomerUserDetails;
import com.seedtoserve.repository.OrderRepository;
import com.seedtoserve.service.PaymentVerificationService;
import com.seedtoserve.service.RazorpayPaymentService;
import com.seedtoserve.enums.OrderStatus;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class PaymentController {

    @Autowired
    private RazorpayPaymentService razorpayPaymentService;

    @Autowired
    private PaymentVerificationService paymentVerificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RazorpayConfig razorpayConfig;

    // Create Payment
    @PostMapping("/create-payment/{orderId}")
    public ResponseEntity<?> createPayment(@PathVariable Integer orderId, Authentication authentication) {
        try {
            CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
            Long customerId = userDetails.getCustomer().getId();

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found!"));

            if (!order.getCustomer().getId().equals(customerId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Order does not belong to this customer!"));
            }

            long totalPaise = Math.round(order.getTotalAmount() * 100);
            CreatePaymentResponseDto dto = razorpayPaymentService.createPayment(customerId, totalPaise);

            // Save Razorpay Order ID and status
            order.setRazorpayOrderId(dto.getRazorpayOrderId());
            order.setStatus(OrderStatus.CREATED);  // ✅ Fixed: use enum directly
            orderRepository.save(order);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Razorpay order created successfully!");
            response.put("razorpayOrderId", dto.getRazorpayOrderId());
            response.put("amount", order.getTotalAmount());
            response.put("currency", "INR");
            response.put("orderId", orderId);
            response.put("key", razorpayConfig.getKey());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Verify Payment
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody VerifyPaymentRequestDto req) {
        try {
            boolean ok = paymentVerificationService.verifyAndMarkPaid(
                    req.getRazorpayOrderId(),
                    req.getRazorpayPaymentId(),
                    req.getRazorpaySignature()
            );

            if (ok) {
                return ResponseEntity.ok(Map.of("message", "Payment verified successfully"));
            } else {
                return ResponseEntity.status(400).body(Map.of("error", "Invalid signature"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
