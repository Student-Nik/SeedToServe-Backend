package com.seedtoserve.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import com.seedtoserve.dto.CategoryDTO;
import com.seedtoserve.model.Category;
import com.seedtoserve.model.Customer;
import com.seedtoserve.repository.CategoryRepository;
import com.seedtoserve.repository.CustomerRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository; 
	
	@Autowired
	private CustomerService customerService;
	
	// Add a Category
	
	public ResponseEntity<Map<String, Object>> addCategory(CategoryDTO categoryDto) {

	    Customer customer = customerService.getLoggedInCustomer();

	    Optional<Category> existingCategory =
	            categoryRepository.findByNameAndCustomerId(
	                    categoryDto.getName(),
	                    customer.getId()
	            );

	    if (existingCategory.isPresent()) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body(Map.of(
	                        "message", "This category already exists!"
	                ));
	    }

	    Category category = new Category();

	    category.setName(categoryDto.getName());
	    category.setDescription(categoryDto.getDescription());
	    category.setCustomer(customer);

	    categoryRepository.save(category);

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(Map.of(
	                    "message", "Category added successfully!",
	                    "category", category
	            ));
	}
	
	// Delete Category
	
	@Transactional
	public ResponseEntity<String> deleteCategory(String name) {

	    Customer customer = customerService.getLoggedInCustomer();

	    Optional<Category> existingCategory =
	            categoryRepository.findByNameAndCustomerId(
	                    name,
	                    customer.getId()
	            );

	    if (existingCategory.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Category not found!");
	    }

	    categoryRepository.deleteByNameAndCustomerId(
	            name,
	            customer.getId()
	    );

	    return ResponseEntity.status(HttpStatus.ACCEPTED)
	            .body("Category deleted successfully!");
	}
	
	// Update Category : Re-assign the values.

	@Transactional
	public ResponseEntity<Map<String, Object>> updateCategory(
	        CategoryDTO categoryDto,
	        String name) {

	    Customer customer = customerService.getLoggedInCustomer();

	    Optional<Category> existingCategory =
	            categoryRepository.findByNameAndCustomerId(
	                    name,
	                    customer.getId()
	            );

	    if (existingCategory.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of(
	                        "message", "Category not found!"
	                ));
	    }

	    Category category = existingCategory.get();

	    category.setName(categoryDto.getName());
	    category.setDescription(categoryDto.getDescription());

	    categoryRepository.save(category);

	    return ResponseEntity.status(HttpStatus.OK)
	            .body(Map.of(
	                    "message", "Category updated successfully!",
	                    "category", category
	            ));
	}
	
	// Show Categories
	
	public List<Category> showCategories() {

	    Customer customer = customerService.getLoggedInCustomer();

	    return categoryRepository.findByCustomerId(customer.getId());
	}
	
	
	

	
	
	
	
	
	
}
