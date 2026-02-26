package com.seedtoserve.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.CategoryDTO;
import com.seedtoserve.model.Category;
import com.seedtoserve.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/farmer/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	// Add Category
	
	@PostMapping("/add/category")
	public ResponseEntity<Map<String,Object>> addCategory(@Valid @RequestBody CategoryDTO categoryDto){
		return categoryService.addCategory(categoryDto);
	}
	
	// Delete Category
	
	@DeleteMapping("/delete/category/{name}")
	public ResponseEntity<String> deleteCategory(@PathVariable String name){
		return categoryService.deleteCategory(name);
	}
	
	// Update Category
	
	@PutMapping("/update/category/{name}")
	public ResponseEntity<Map<String, Object>> updateCategory(
	        @RequestBody CategoryDTO categoryDto,
	        @PathVariable String name){
	    return categoryService.updateCategory(categoryDto, name);
	}
	
	// Show Category
	
	@GetMapping("/show/categories")
	public List<Category> showCategories() {
		return categoryService.showCategories();
	}

}
