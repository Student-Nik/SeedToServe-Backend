package com.seedtoserve.dto;

import java.time.LocalDateTime;

import com.seedtoserve.enums.OrderStatus;
import com.seedtoserve.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderResponse {

	private int orderId;

	private String customerName;

	private double totalAmount;

	private String paymentMethod;

	private OrderStatus orderStatus;

	private PaymentStatus paymentStatus;

	private LocalDateTime orderDate;
}