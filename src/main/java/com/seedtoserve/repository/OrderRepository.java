package com.seedtoserve.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{

	List<Order> findByCustomer(Customer customer);

	Optional<Customer> findByIdAndCustomerId(int orderId, int customerId);

	
	
}
