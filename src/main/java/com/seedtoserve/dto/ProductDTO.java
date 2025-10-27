package com.seedtoserve.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

	private int id;
	
	@NotBlank(message="Product name is Required!")
	@Size(min = 3, max = 10, message = "Product Name must be between 3 and 10 characters")
	private String name;
	
	@NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    @Max(value = 100000, message = "Price cannot exceed 100000")
	private double price;
	
	@Min(value = 0, message = "Stock cannot be negative")
	private int stock;
	
	@NotNull(message="Category is Required!")
	private Long categoryId;
	
	@NotNull(message="Category is Required!")
	private String categoryName;
	
	private String imageBase64;
	
	@NotBlank(message="Description is Required!")
	private String description;
	
}
