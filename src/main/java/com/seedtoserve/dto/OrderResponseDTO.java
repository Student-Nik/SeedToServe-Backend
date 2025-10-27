package com.seedtoserve.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.seedtoserve.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

	private int orderId;
    private Long customerId;
    private List<OrderItemDTO> items;
    private double totalAmount;
    private double totalItems;
    private OrderStatus status;
    private String shippingAddress;
    private String orderDate;
    private String expectedDeliveryDate;
}
