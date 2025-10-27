package com.seedtoserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

	@NotBlank(message="Category name is Required!")
	@Size(min = 3, max = 20, message = "Category Name must be between 3 and 10 characters")
	private String name;
	
	@NotBlank(message="Category Description is Required!")
	@Size(min = 10, max = 150, message = "Category Description must be between 10 and 100 characters")
	private String description;
}
