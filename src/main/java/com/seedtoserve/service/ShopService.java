package com.seedtoserve.service;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.ProductDTO;
import com.seedtoserve.repository.ProductRepository;

@Service
public class ShopService {

	@Autowired
	private ProductRepository productRepository;

	// Show all products in Shop
	public List<ProductDTO> showShopProducts() {

		return productRepository.findAll().stream().map(product -> {

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

			// Convert image to Base64
			if (product.getImage() != null && product.getImage().length > 0) {

				String base64Image = Base64.getEncoder().encodeToString(product.getImage());

				dto.setImageBase64("data:image/jpeg;base64," + base64Image);
			}

			return dto;
		}).toList();
	}

	// Fetch Individual product

	public ResponseEntity<ProductDTO> showIndividualProduct(int id) {

		return productRepository.findById(id).map(product -> {

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

			if (product.getImage() != null && product.getImage().length > 0) {

				String base64Image = Base64.getEncoder().encodeToString(product.getImage());

				dto.setImageBase64("data:image/jpeg;base64," + base64Image);
			}

			return ResponseEntity.ok(dto);
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}

}
