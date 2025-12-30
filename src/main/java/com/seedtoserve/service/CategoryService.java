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
import com.seedtoserve.repository.CategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository; 
	
	// Add a Category
	
	public ResponseEntity<Map<String, Object>> addCategory(CategoryDTO categoryDto){
		
		Optional<Category> isExistingCategory = categoryRepository.findByName(categoryDto.getName());
		
		if(isExistingCategory.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body(Map.of(
	                        "message", "This category already exists!"
	                ));
		}else {
			Category category = new Category();
			category.setName(categoryDto.getName());
			category.setDescription(categoryDto.getDescription());
			
			categoryRepository.save(category);
			
			return ResponseEntity.status(HttpStatus.OK)
	                .body(Map.of(
	                        "message", "Category updated successfully!",
	                        "category", categoryDto
	                ));
		}
	}
	
	// Delete Category
	
	@Transactional
	@Modifying
	public ResponseEntity<String> deleteCategory(String name){
		
		Optional<Category> isExistingCategory = categoryRepository.findByName(name);
		
		if(isExistingCategory.isPresent()) {
			categoryRepository.deleteByName(name);
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					  .body("Category deleted Succesfully!");
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Category not Found!");
		}
	}
	
	// Update Category : Re-assign the values.

	@Transactional
	public ResponseEntity<Map<String,Object>> updateCategory(CategoryDTO categoryDto, String name) {
		
		Optional<Category> isExistingCategory = categoryRepository.findByName(name);
		
		if(isExistingCategory.isPresent()) {
		
			Category category = isExistingCategory.get();
			
			category.setName(categoryDto.getName());
			category.setDescription(categoryDto.getDescription());
			
			categoryRepository.save(category);
			
			return ResponseEntity.status(HttpStatus.OK)
	                .body(Map.of(
	                        "message", "Category updated successfully!",
	                        "category", categoryDto
	                ));
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(Map.of(
	                    "message", "Category not found!"
	            ));
	}
	
	// Show Categories
	
	public List<CategoryDTO> showCategories() {
		
		// Here, it exposes all records of the table.
		// return categoryRepository.findAll();
		
		// Use DTO to return only those records you need.
		
		return categoryRepository.findAll()
				.stream()
				.map(cat -> {
				  CategoryDTO dto = new CategoryDTO();
		          dto.setName(cat.getName());
		          dto.setDescription(cat.getDescription());
		          return dto;
				}).toList();
		
	}
	

	
	
	
	
	
	
}
