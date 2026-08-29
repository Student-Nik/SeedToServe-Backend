package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBoyLoginResponse {

	private String message;
    private String token;
    private int deliveryBoyId;
    private String firstName;
    private String lastName;
    private String email;
}
