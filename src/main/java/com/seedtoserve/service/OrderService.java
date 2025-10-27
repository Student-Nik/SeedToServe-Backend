package com.seedtoserve.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.OrderDTO;
import com.seedtoserve.dto.OrderItemDTO;
import com.seedtoserve.dto.OrderResponseDTO;
import com.seedtoserve.enums.OrderStatus;
import com.seedtoserve.model.AddressDetails;
import com.seedtoserve.model.CartItem;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Order;
import com.seedtoserve.model.OrderItem;
import com.seedtoserve.model.Product;
import com.seedtoserve.repository.AddressRepository;
import com.seedtoserve.repository.CartItemRepository;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.repository.OrderRepository;
import com.seedtoserve.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.Data;

@Service
public class OrderService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	// Create Order
	
	@Transactional
	public ResponseEntity<String> createOrder(OrderDTO orderDto){
		
		// 1. Fetch Customer
		
		Customer customer = customerRepository.findById(orderDto.getCustomerId())
				.orElseThrow( () -> new RuntimeException("Customer not found!"));
		
		// 2. Fetch Address
		
		AddressDetails addressDetails = addressRepository.findById((int) orderDto.getAddressId())
				.orElseThrow( () -> new RuntimeException("Address not found!"));
		
		// 3. validate address belongs to customer
		
		if(!addressDetails.getCustomer().getId().equals(customer.getId())) {
			throw new RuntimeException("Address does not belong to this customer!");
		}
		
		// 4. Fetch Cart Items
		
		List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
		if(cartItems.isEmpty()) {
			throw new RuntimeException("Cart is Empty");
		}
		
		// 5. Create Order
		
		Order order = new Order();
		order.setCustomer(customer);
		order.setAddressDetails(addressDetails);
		order.setPaymentMethod(orderDto.getPaymentMethod());
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);
		
		// totalAmount and orderDetails PENDING
		
		double totalAmount = 0.0;
		
		// 6. Convert CartItems into OrderItems
		
		for(CartItem cartItem : cartItems) {
			
			Product product = cartItem.getProduct();
			
			 // check stock
	        if (product.getStock() < cartItem.getQuantity()) {
	            throw new RuntimeException("Insufficient stock for " + product.getName()+", Available Stock :"+product.getStock());
	        }

	        // reduce stock
	        product.setStock(product.getStock() - cartItem.getQuantity());
	        productRepository.save(product);
	        
	        // create OrderItem
	        
	        OrderItem orderItem = new OrderItem();
	        
	        orderItem.setProduct(product);
	        orderItem.setQuantity(cartItem.getQuantity());
	        orderItem.setPrice(product.getPrice());
	        
	        // List<OrderItem> items = new ArrayList<>();
	        // items.add(orderItem);
	        // order.setOrderItems(items); -> bug : it adds last product only
	        
	        orderItem.setOrder(order);
	        order.getOrderItems().add(orderItem);

	        totalAmount += product.getPrice() * cartItem.getQuantity();
	         
		}
		
		order.setTotalAmount(totalAmount);
		
		orderRepository.save(order);
		
		cartItemRepository.deleteByCustomer(customer);
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Order Created Successfully!");
	
	}
	
	// Cancel Order
	
	@Transactional
	public ResponseEntity<String> cancelOrder(int orderId, Long long1){
		
		// check order exists
		
		Order order = orderRepository.findById(orderId)
				.orElseThrow( () -> new RuntimeException("Order not found!"));
		
		// validate order belongs to customer
		
		if(!order.getCustomer().getId().equals(Long.valueOf(long1))) {
		    throw new RuntimeException("You cannot cancel this order!");
		}
		
		// check status 
		
		if(order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
			throw new RuntimeException("Order already shipped/delivered. Cannot cancel!");
		}
		
		// restore stock 
		
		for (OrderItem item : order.getOrderItems()) { 
			Product product = item.getProduct(); 
			product.setStock(product.getStock() + item.getQuantity()); 
			productRepository.save(product); 
		}
		
		// refund (only if paid) 
		if (order.getStatus() == OrderStatus.PAID) {
			// Call Razorpay refund API here. Payment Integration pending
		}
		
		order.setStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);
		return ResponseEntity.status(HttpStatus.OK)
				.body("Order Cancelled Successfully!");
	}
	
	// Show Order
	
	public List<OrderResponseDTO> getOrdersByCustomer(Long long1) {

	    // 1. Check customer exists
	    Customer customer = customerRepository.findById((long) long1)
	            .orElseThrow(() -> new RuntimeException("Customer not found!"));

	    // 2. Fetch all orders for that customer
	    List<Order> orders = orderRepository.findByCustomer(customer);
	    if (orders.isEmpty()) {
	        throw new RuntimeException("No orders found for this customer!");
	    }

	    // 3. Map each order to OrderResponseDTO
	    return orders.stream().map(order -> {

	        List<OrderItem> orderItems = order.getOrderItems();
	        if (orderItems.isEmpty()) {
	            throw new RuntimeException("Order has no items!");
	        }

	        // Map order items to DTOs
	        List<OrderItemDTO> itemDTOs = orderItems.stream().map(item -> {
	            Product product = item.getProduct();

	            // Encode image to Base64
	            String imageBase64 = null;
	            if (product.getImage() != null && product.getImage().length > 0) {
	                imageBase64 = "data:image/jpeg;base64," +
	                        Base64.getEncoder().encodeToString(product.getImage());
	            }

	            OrderItemDTO dto = new OrderItemDTO();
	            dto.setProductId(product.getId()); 
	            dto.setProductName(product.getName());
	            dto.setPrice(product.getPrice());
	            dto.setQuantity(item.getQuantity());
	            dto.setTotalPrice(product.getPrice() * item.getQuantity());
	            dto.setProductImage(imageBase64);
	            
	            return dto;
	        }).toList();

	        // Calculate totals
	        double totalAmount = itemDTOs.stream()
	                .mapToDouble(OrderItemDTO::getTotalPrice)
	                .sum();

	        int totalItems = itemDTOs.stream()
	                .mapToInt(OrderItemDTO::getQuantity)
	                .sum();

	        // Build full address string
	        
	        AddressDetails address = order.getAddressDetails(); 
	        String fullAddress = address.getHouseNoOrStreet() + ", " +
	                             address.getVillageOrTown() + ", " +
	                             address.getDistrict() + ", " +
	                             address.getState() + ", " +
	                             address.getPincode();

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy");
	        
	        // Build response
	        
	        OrderResponseDTO response = new OrderResponseDTO();
	        response.setOrderId(order.getId());
	        response.setCustomerId(customer.getId());
	        response.setItems(itemDTOs);
	        response.setTotalAmount(totalAmount);
	        response.setTotalItems(totalItems);
	        response.setStatus(order.getStatus());
	        response.setShippingAddress(fullAddress);

	        // Order date
	        response.setOrderDate(order.getOrderDate().format(formatter));

	        // Expected delivery date = 7 days after order date
	        response.setExpectedDeliveryDate(order.getOrderDate().plusDays(7).format(formatter));

	        return response;

	    }).toList();
	}

}
