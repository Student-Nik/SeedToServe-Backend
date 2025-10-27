package com.seedtoserve.dto;

import com.seedtoserve.model.Mail;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAndSendEmailRequestDTO {

	@Valid
	private CustomerDTO customerDto;
	
	@Valid
	private Mail mail;
}
