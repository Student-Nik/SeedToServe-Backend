package com.seedtoserve.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seedtoserve.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

	Optional<Product> findByName(String name);
	
	Optional<Product> deleteByName(String name);
	
	Optional<Product> findById(Long id);
	
}
