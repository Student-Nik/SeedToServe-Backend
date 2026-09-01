package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBoyOrderItemResponse {

	private String productName;

	private byte[] productImage;

	private int quantity;

	private double price;

}
