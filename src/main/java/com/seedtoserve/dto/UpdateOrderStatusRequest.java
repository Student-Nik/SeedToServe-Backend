package com.seedtoserve.dto;

import com.seedtoserve.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

	@NotNull(message = "Order status is required")
	private OrderStatus orderStatus;
}
