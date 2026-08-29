package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBoyForAdminResponse {

	private int id;

	private String firstName;

	private String lastName;

	private String mobileNo;

	private String email;

	private boolean available;
}
