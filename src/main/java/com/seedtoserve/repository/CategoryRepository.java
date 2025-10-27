package com.seedtoserve.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seedtoserve.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

	Optional<Category> findByName(String name);
	
	Optional<Category> deleteByName(String name);
	
	Optional<Category> findById(Long id);
}
