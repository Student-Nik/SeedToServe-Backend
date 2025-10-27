package com.seedtoserve.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

	@NotBlank(message="Registration Type is required!")
	private String registrationType;
	
	@NotBlank(message="First Name is Required!")
	@Size(min = 3, max = 10, message = "First Name must be between 3 and 10 characters")
	private String firstName;
	
	@NotBlank(message="Last Name is Required!")
	@Size(min = 3, max = 10, message = "Last Name must be between 3 and 10 characters")
	private String lastName;
	
	@NotBlank(message="Email is Required!")
	@Email(message="Email Should be well-formed")
	private String email;
	
	@NotBlank(message="Password is Required!")
	@Size(min=8, max=16, message="Must be in between 8 and 16 characters")
	@Pattern(
		        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		        message = "Password must be at least 8 characters long and include a letter, a number, and a special character"
		    )
	private String password;
	
	@NotBlank(message="Password is Required!")
	@Size(min=8, max=16, message="Must be in between 8 and 16 characters")
	@Pattern(
		        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		        message = "Password must be at least 8 characters long and include a letter, a number, and a special character"
		    )
	private String confirmPassword;
}
