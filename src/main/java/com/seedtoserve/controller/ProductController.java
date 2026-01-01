package com.seedtoserve.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.seedtoserve.dto.ProductDTO;
import com.seedtoserve.model.Product;
import com.seedtoserve.service.ProductService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/farmer/products")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	/*
	  Why @RequestPart?
      @RequestBody only works with pure JSON (application/json).
      @RequestParam works for simple form fields or file only.
      @RequestPart is designed for multipart/form-data where one part is
       JSON (your DTO) and the other part(s) are file(s).
       
	 */
	
	// Add a Product
	
	@PostMapping(
		    value = "/add/product",
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
		)
		public ResponseEntity<Map<String, Object>> addProduct(
		        @Valid
		        @RequestPart("productDto") ProductDTO productDto,

		        @RequestPart(value = "image", required = false)
		        MultipartFile imageFile
		) {
		    try {
		        return productService.addProduct(productDto, imageFile);
		    } catch (IOException e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(Map.of(
		                        "message", "Error while processing image",
		                        "error", e.getMessage()
		                ));
		    } catch (RuntimeException e) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                .body(Map.of("message", e.getMessage()));
		    }
		}



	// Delete a Product
	
	@DeleteMapping("/delete/product/{name}")
	public ResponseEntity<String> deleteProduct(@PathVariable String name){
		return productService.deleteProduct(name);
	}
	
	// Update a Product
	
	@PutMapping(value = "/update/product/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> updateProduct(
	        @RequestPart ProductDTO productDto,
	        @RequestPart(required = false) MultipartFile imageFile,
	        @PathVariable String name) throws IOException {
	    return productService.updateProduct(productDto, imageFile, name);
	}

	
	// Show Products Details (name, price, stock, category )
	
	@GetMapping("/show/products")
	public List<ProductDTO> showProducts(){
		return productService.showProducts();
	}
	
	
}
