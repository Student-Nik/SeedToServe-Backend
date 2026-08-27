package com.seedtoserve.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.seedtoserve.enums.OrderStatus;
import com.seedtoserve.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderDetailsResponse {

	private int orderId;

	private String customerName;

	private double totalAmount;

	private String paymentMethod;

	private PaymentStatus paymentStatus;

	private OrderStatus orderStatus;

	private LocalDateTime orderDate;

	private BuyerAddressForAdminResponse address;

	private List<AdminOrderItemResponse> items;
}
