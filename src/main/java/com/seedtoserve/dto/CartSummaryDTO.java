package com.seedtoserve.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryDTO {

	private List<CartItemResponse> items;
    private double totalAmount;
    private int totalItems;
}
