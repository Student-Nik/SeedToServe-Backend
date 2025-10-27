package com.seedtoserve.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="mail_details")
public class Mail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;	
	
	private String name;
	private String sender;
	
	@Email(message="Invalid Email Format")
    private String recipient;
	
    private String subject;
    private LocalDateTime sentAt;
    private boolean success;
    
    private String registrationType;
}
