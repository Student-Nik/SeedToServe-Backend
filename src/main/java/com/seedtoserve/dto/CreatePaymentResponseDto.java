package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentResponseDto {

	 private String razorpayOrderId; 
	 private Integer amount;
	 private String currency;
	 private String key; 
	 
}
