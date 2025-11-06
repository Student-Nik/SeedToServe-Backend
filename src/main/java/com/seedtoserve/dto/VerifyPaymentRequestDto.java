package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequestDto {

	 private String razorpayPaymentId;
	 private String razorpayOrderId;
	 private String razorpaySignature;
}
