package com.seedtoserve.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.seedtoserve.config.RazorpayConfig;
import com.seedtoserve.dto.CreatePaymentResponseDto;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Payment;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.repository.PaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class RazorpayPaymentService {
	
	@Autowired
	private RazorpayConfig razorpayConfig;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Transactional
	public CreatePaymentResponseDto createPayment(Long customerId, Long amountInPaise) throws RazorpayException {
		
		RazorpayClient razorpayClient = new RazorpayClient(razorpayConfig.getKey(), razorpayConfig.getSecret());
		
		 JSONObject orderRequest = new JSONObject();
		 orderRequest.put("amount", amountInPaise); // paise
		 orderRequest.put("currency", "INR");
		 orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
		
		 Order order = razorpayClient.orders.create(orderRequest);
		 
		 Customer customer = customerRepository.findById((long) customerId)
	                .orElseThrow(() -> new RuntimeException("Customer not found"));
		 
		 Payment payment = new Payment();
		 payment.setCustomer(customer);
		 payment.setAmount(amountInPaise / 100.0);
		 payment.setRazorpayOrderId(order.get("id"));
		 payment.setStatus("CREATED");
		 
		 paymentRepository.save(payment);
		 
		 CreatePaymentResponseDto dto = new CreatePaymentResponseDto();
		 dto.setRazorpayOrderId(order.get("id"));
		 dto.setAmount(order.get("amount"));
		 dto.setCurrency(order.get("currency"));
		 dto.setKey(razorpayConfig.getKey());
		 return dto;
	}

}
