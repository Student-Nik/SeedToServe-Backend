package com.seedtoserve.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seedtoserve.model.CartItem;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String>{

	Optional<CartItem> findByCustomerAndProduct(Customer customer,Product product);

	List<CartItem> findByCustomer(Customer customer);
	
	void deleteByCustomer(Customer customer);
	
}
