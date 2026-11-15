package com.seedtoserve.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketPriceReferenceRequest {

	private String productName;

	private String marketName;

	private String district;

	private String state;

	private BigDecimal minPrice;

	private BigDecimal modalPrice;

	private BigDecimal maxPrice;

	private String unit;

	private LocalDate priceDate;

	private String source;
}
