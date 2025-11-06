package com.seedtoserve.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class RazorpayConfig {
	
	@Value("${razorpay.key}")
	private String key;
	
	@Value("${razorpay.secret}")
	private String secret;

}
