package com.seedtoserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDetailsDTO {

	@NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number, must be 10 digits starting with 6-9")
    private String mobileNo;

    @NotBlank(message = "House/Street is required")
    @Size(max = 150, message = "House/Street cannot exceed 150 characters")
    private String houseNoOrStreet;

    @NotBlank(message = "Village/Town is required")
    @Size(max = 100, message = "Village/Town cannot exceed 100 characters")
    private String villageOrTown;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District cannot exceed 100 characters")
    private String district;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode, must be 6 digits")
    private String pincode;

    @Positive(message = "Customer ID must be a positive number")
    private int customerId;
    
}
