package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

	private int productId;
    private String productName;
    private int quantity;
    private double price;  
    private double totalPrice; 
    private String productImage;    
}
