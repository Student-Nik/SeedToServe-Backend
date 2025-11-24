package com.seedtoserve.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {

	@NotBlank(message="Name is Required!")
	@Size(min = 3, max = 20, message = "Name must be between 3 and 10 characters")
	private String fullName;
	
	@Email(message="Email should be in well-format")
	private String email;
	
	@Pattern(regexp="^[6-9]\\d{9}$", message="Invalid phone number")
	private String phoneNumber;
	
	@Size(min = 20, max = 200, message = "Description must be between 20 and 200 characters")
	private String description;
	
}
