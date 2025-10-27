package com.seedtoserve.dto;

import java.time.LocalDateTime;

import com.seedtoserve.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {

	private Long orderId;
    private double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
}
