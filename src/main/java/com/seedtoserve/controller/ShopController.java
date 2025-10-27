package com.seedtoserve.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.ProductDTO;
import com.seedtoserve.service.ShopService;

@RestController
public class ShopController {

	@Autowired
	private ShopService shopService;
	
	// Fetching all products from database in shop 
	
	/*
	@GetMapping("/shop/products")
	public List<ProductDTO> shopProducts(){
		return productService.showProducts();
	}*/
	
	// Here, we need to sort products by category name
	// means, FRUITS -> ALL PRODUCTS UNDER FRUITS CATEGORY
	//        VEGETABLES -> ALL PRODUCTS UNDER VEGETABLES CATEGORY
	
	@GetMapping("/buy/products")
	public Map<String, List<ProductDTO>> getProductsByCategory() {
	    return shopService.showAllProducts();
	}
	
	// TreeMap
	// always keeps keys in sorted order according to their natural ordering

	@GetMapping("/show/product/{id}")
	public ResponseEntity<ProductDTO> getProductDetails(@PathVariable int id){
		return shopService.showIndividualProduct(id);
	}
}
