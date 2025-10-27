package com.seedtoserve.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AddressDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String fullName;
	private String mobileNo;
	private String houseNoOrStreet;
    private String villageOrTown;
    private String district;
    private String state;
    private String pincode;
	
    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;
    
}
