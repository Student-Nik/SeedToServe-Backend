package com.seedtoserve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.ContactDTO;
import com.seedtoserve.service.ContactService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/contact")
public class ContactController {
	
	@Autowired
	private ContactService contactService;
	
	@PostMapping("/send/concern")
	public ResponseEntity<String> createContact(@Valid @RequestBody ContactDTO contactDto){
		return contactService.createContact(contactDto);
	}
}
