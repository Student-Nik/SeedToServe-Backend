package com.seedtoserve.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.seedtoserve.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="orders")
public class Order {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne // Many Order one Customer
	private Customer customer;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
	private LocalDateTime orderDate;
	
    private double totalAmount;
    
    private String paymentMethod;
    
    @ManyToOne // Many Order one Address
    private AddressDetails addressDetails;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // Many OrderItems One Order
    private List<OrderItem> orderItems = new ArrayList<>();
    
}
