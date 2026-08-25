package com.seedtoserve.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.seedtoserve.dto.ProductDTO;
import com.seedtoserve.model.Category;
import com.seedtoserve.model.Customer;
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

	@Autowired
	private CustomerService customerService;

	// =========================
	// ADD PRODUCT
	// =========================

	public ResponseEntity<Map<String, Object>> addProduct(ProductDTO productDto, MultipartFile imageFile)
			throws IOException {

		// Get logged-in customer from JWT
		Customer customer = customerService.getLoggedInCustomer();

		// Check whether this customer already has this product
		Optional<Product> existing = productRepository.findByNameAndCustomerId(productDto.getName(), customer.getId());

		if (existing.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Product already exists!"));
		}

		// Fetch category
		Category category = categoryRepository.findById(productDto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found with id: " + productDto.getCategoryId()));

		// Convert image to byte[]
		byte[] imageBytes = null;

		if (imageFile != null && !imageFile.isEmpty()) {
			imageBytes = imageFile.getBytes();
		}

		// Create product
		Product product = new Product();

		product.setName(productDto.getName());
		product.setPrice(productDto.getPrice());
		product.setStock(productDto.getStock());
		product.setDescription(productDto.getDescription());
		product.setCategory(category);
		product.setImage(imageBytes);

		// VERY IMPORTANT
		// Associate product with logged-in customer
		product.setCustomer(customer);

		productRepository.save(product);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("message", "Product details added successfully!", "product", productDto));
	}

	// =========================
	// DELETE PRODUCT
	// =========================

	@Transactional
	public ResponseEntity<String> deleteProduct(String productName) {

		// Get logged-in customer
		Customer customer = customerService.getLoggedInCustomer();

		// Find product belonging to logged-in customer
		Optional<Product> existingProduct = productRepository.findByNameAndCustomerId(productName, customer.getId());

		if (existingProduct.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
		}

		productRepository.deleteByNameAndCustomerId(productName, customer.getId());

		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Product deleted successfully!");
	}

	// =========================
	// UPDATE PRODUCT
	// =========================

	@Transactional
	public ResponseEntity<Map<String, Object>> updateProduct(ProductDTO productDto, MultipartFile imageFile,
			String name) throws IOException {

		// Get logged-in customer
		Customer customer = customerService.getLoggedInCustomer();

		// Find product owned by logged-in customer
		Optional<Product> existingProduct = productRepository.findByNameAndCustomerId(name, customer.getId());

		if (existingProduct.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product not found!"));
		}

		Product product = existingProduct.get();

		// Fetch category
		Category category = categoryRepository.findById(productDto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found with id: " + productDto.getCategoryId()));

		// Update product
		product.setName(productDto.getName());
		product.setPrice(productDto.getPrice());
		product.setStock(productDto.getStock());
		product.setDescription(productDto.getDescription());
		product.setCategory(category);

		// Update image only if new image is provided
		if (imageFile != null && !imageFile.isEmpty()) {
			product.setImage(imageFile.getBytes());
		}

		productRepository.save(product);

		return ResponseEntity.status(HttpStatus.OK)
				.body(Map.of("message", "Product updated successfully!", "product", productDto));
	}

	// =========================
	// SHOW PRODUCTS
	// =========================

	public List<ProductDTO> showProducts() {

		Customer customer = customerService.getLoggedInCustomer();

		// Only products belonging to logged-in customer
		return productRepository.findByCustomerId(customer.getId()).stream().map(product -> {

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

			// Convert image bytes to Base64
			if (product.getImage() != null && product.getImage().length > 0) {

				String base64Image = Base64.getEncoder().encodeToString(product.getImage());

				dto.setImageBase64("data:image/jpeg;base64," + base64Image);
			}

			return dto;
		}).toList();
	}
}