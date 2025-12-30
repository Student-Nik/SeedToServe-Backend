package com.seedtoserve.service;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.seedtoserve.dto.CategoryDTO;
import com.seedtoserve.dto.ProductDTO;
import com.seedtoserve.model.Category;
import com.seedtoserve.model.Product;
import com.seedtoserve.repository.CategoryRepository;
import com.seedtoserve.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	// Add Product
	public ResponseEntity<Map<String, Object>> addProduct(ProductDTO productDto,MultipartFile imageFile) throws IOException {

        // Check if product exists
        Optional<Product> existing = productRepository.findByName(productDto.getName());
        if (existing.isPresent()) {
            throw new RuntimeException("Product already exists!");
        }

        // Fetch category
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + productDto.getCategoryId()));

        // Convert MultipartFile to byte[]
        byte[] imageBytes = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageBytes = imageFile.getBytes();
        }

        // Map to entity
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setCategory(category);
        product.setImage(imageBytes);
        product.setDescription(productDto.getDescription());

        productRepository.save(product);
        
        return ResponseEntity.status(HttpStatus.CREATED)
        		.body(Map.of("Product details added successfully!",productDto));
    }
	
	// Delete Product
	
	@Transactional
	@Modifying
	public ResponseEntity<String> deleteProduct(String productName){
		
		Optional<Product> isExistingProduct = productRepository.findByName(productName);
		
		if(isExistingProduct.isPresent()) {
			productRepository.deleteByName(productName);
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body("Product deleted Successfully!");
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Product not found!");
		}
	}
	
	// Update Product : Re-assign the values
	
	@Transactional
	public ResponseEntity<Map<String,Object>> updateProduct(ProductDTO productDto, MultipartFile imageFile, String name) throws IOException{
		
		Optional<Product> isExistingProduct = productRepository.findByName(name);
		
		if(isExistingProduct.isPresent()) {
			
			// Fetch category
	        Category category = categoryRepository.findById(productDto.getCategoryId())
	                .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDto.getCategoryName()));

	        // Convert MultipartFile to byte[]
	        //byte[] imageBytes = null;
	        //if (imageFile != null && !imageFile.isEmpty()) {
	        //    imageBytes = imageFile.getBytes();
	        //}
	        
			// Update the product : get that product details and re-assign it. 
			Product product = isExistingProduct.get();
			
			product.setName(productDto.getName());
			product.setPrice(productDto.getPrice());
			product.setStock(productDto.getStock());
			product.setCategory(category);
			 
			// Only update image if a new one is provided
	        if (imageFile != null && !imageFile.isEmpty()) {
	            product.setImage(imageFile.getBytes());
	        }
	        
			
			productRepository.save(product);
			
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("Product updated successfully!",productDto));
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("Product Not found!",productDto));
		}
	}
	
	// Show Products Details (id, name, price, stock, category id and name and image)
	
	public List<ProductDTO> showProducts() {
	    return productRepository.findAll()
	            .stream()
	            .map(product -> {
	                ProductDTO dto = new ProductDTO();
	                dto.setId(product.getId());
	                dto.setName(product.getName());
	                dto.setPrice(product.getPrice());
	                dto.setStock(product.getStock());
	                dto.setDescription(product.getDescription());

	                if (product.getCategory() != null) {
	                	dto.setCategoryId(product.getCategory().getId());
	                    dto.setCategoryName(product.getCategory().getName());
	                }

	                // send image URL
	                // dto.setImageUrl("/api/products/" + product.getId() + "/image");
	                
	               // Convert image bytes to Base64 and set in DTO
	                if (product.getImage() != null && product.getImage().length > 0) {
	                    String base64Image = Base64.getEncoder().encodeToString(product.getImage());
	                    dto.setImageBase64("data:image/jpeg;base64," + base64Image); // or png
	                }
	                return dto;
	            })
	            .toList();
	}
	
}
