package com.seedtoserve.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.MarketPriceReferenceRequest;
import com.seedtoserve.dto.MarketPriceReferenceResponse;
import com.seedtoserve.service.MarketPriceReferenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/market-prices")
public class MarketPriceReferenceController {

	private final MarketPriceReferenceService marketPriceReferenceService;
	
	// Add market price reference
    @PostMapping
    public ResponseEntity<MarketPriceReferenceResponse> addMarketPrice(
            @RequestBody MarketPriceReferenceRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(marketPriceReferenceService.addMarketPrice(request));
    }

    // Get market prices by commodity and location
    @GetMapping("/reference")
    public ResponseEntity<List<MarketPriceReferenceResponse>> getMarketPrices(
            @RequestParam String productName,
            @RequestParam String district,
            @RequestParam String state) {

        return ResponseEntity.ok(
        		marketPriceReferenceService.getMarketPrices(productName, district, state));
    }
}
