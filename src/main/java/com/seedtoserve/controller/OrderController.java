package com.seedtoserve.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.OrderDTO;
import com.seedtoserve.dto.OrderResponseDTO;
import com.seedtoserve.model.Customer;
import com.seedtoserve.security.CustomerUserDetails;
import com.seedtoserve.service.CustomerService;
import com.seedtoserve.service.OrderService;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    // Create order for logged-in customer
    @PostMapping("/create/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderDTO orderDto) {
        Customer customer = customerService.getLoggedInCustomer();
        return orderService.createOrder(orderDto);
    }

    // Cancel order for logged-in customer
    @PostMapping("/cancel/order/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable int orderId) {
        Customer customer = customerService.getLoggedInCustomer();
        return orderService.cancelOrder(orderId, customer.getId());
    }

    // Show orders for logged-in customer
    @GetMapping("/my/orders")
    public List<OrderResponseDTO> showMyOrders() {
        Customer customer = customerService.getLoggedInCustomer();
        return orderService.getOrdersByCustomer(customer.getId());
    }
    
 // Fetch latest order for the logged-in customer
    @GetMapping("/latest-order")
    public ResponseEntity<?> getLatestOrder(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        try {
            Long customerId = userDetails.getCustomer().getId(); 

            List<OrderResponseDTO> orders = orderService.getOrdersByCustomer(customerId);

            if (orders.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No orders found!"));
            }

            OrderResponseDTO latestOrder = orders.get(orders.size() - 1);
            return ResponseEntity.ok(latestOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
}
