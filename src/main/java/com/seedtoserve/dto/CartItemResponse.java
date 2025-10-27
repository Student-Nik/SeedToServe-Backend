package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

	private int productId;
    private String productName;
    private double price;
    private int quantity;
    private String imageBase64;
    private double subtotal;
}
