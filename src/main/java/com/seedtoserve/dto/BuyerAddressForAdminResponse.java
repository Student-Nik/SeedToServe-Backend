package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyerAddressForAdminResponse {

	private String fullName;
	private String mobileNo;
	private String houseNoOrStreet;
	private String villageOrTown;
	private String district;
	private String state;
	private String pincode;
}
