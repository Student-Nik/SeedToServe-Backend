package com.seedtoserve.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seedtoserve.dto.MarketPriceReferenceRequest;
import com.seedtoserve.dto.MarketPriceReferenceResponse;
import com.seedtoserve.model.MarketPriceReference;
import com.seedtoserve.repository.MarketPriceReferenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketPriceReferenceService {

	private final MarketPriceReferenceRepository marketPriceReferenceRepository;

	// Add market price reference
	public MarketPriceReferenceResponse addMarketPrice(MarketPriceReferenceRequest request) {

		validatePrice(request);

		MarketPriceReference marketPrice = new MarketPriceReference();

		marketPrice.setProductName(request.getProductName().trim());
		marketPrice.setMarketName(request.getMarketName().trim());
		marketPrice.setDistrict(request.getDistrict().trim());
		marketPrice.setState(request.getState().trim());
		marketPrice.setMinPrice(request.getMinPrice());
		marketPrice.setModalPrice(request.getModalPrice());
		marketPrice.setMaxPrice(request.getMaxPrice());
		marketPrice.setUnit(request.getUnit().trim().toUpperCase());
		marketPrice.setPriceDate(request.getPriceDate());
		marketPrice.setSource(request.getSource());

		MarketPriceReference saved = marketPriceReferenceRepository.save(marketPrice);

		return convertToResponse(saved);
	}

	// Get market prices by commodity and location
	public List<MarketPriceReferenceResponse> getMarketPrices(String commodityName, String district, String state) {

		List<MarketPriceReference> prices = marketPriceReferenceRepository
				.findByProductNameIgnoreCaseAndDistrictIgnoreCaseAndStateIgnoreCase(commodityName, district, state);

		return prices.stream().map(this::convertToResponse).toList();
	}

	// Convert Entity to Response DTO
	private MarketPriceReferenceResponse convertToResponse(MarketPriceReference marketPrice) {

		return new MarketPriceReferenceResponse(marketPrice.getProductName(), marketPrice.getMarketName(),
				marketPrice.getDistrict(), marketPrice.getState(), marketPrice.getMinPrice(),
				marketPrice.getModalPrice(), marketPrice.getMaxPrice(), marketPrice.getUnit(),
				marketPrice.getPriceDate(), marketPrice.getSource());
	}

	// Validate market price data
	private void validatePrice(MarketPriceReferenceRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Market price request cannot be empty");
		}

		if (isBlank(request.getProductName()) || isBlank(request.getMarketName()) || isBlank(request.getDistrict())
				|| isBlank(request.getState()) || isBlank(request.getUnit())) {

			throw new IllegalArgumentException("Required fields cannot be empty");
		}

		if (request.getMinPrice() == null || request.getModalPrice() == null || request.getMaxPrice() == null) {

			throw new IllegalArgumentException("Market prices cannot be empty");
		}

		if (request.getMinPrice().signum() <= 0 || request.getModalPrice().signum() <= 0
				|| request.getMaxPrice().signum() <= 0) {

			throw new IllegalArgumentException("Market prices must be greater than zero");
		}

		if (request.getMinPrice().compareTo(request.getModalPrice()) > 0
				|| request.getModalPrice().compareTo(request.getMaxPrice()) > 0) {

			throw new IllegalArgumentException("Price must follow Min <= Modal <= Max");
		}

		if (request.getPriceDate() == null) {
			throw new IllegalArgumentException("Price date is required");
		}

		if (request.getPriceDate().isAfter(java.time.LocalDate.now())) {
			throw new IllegalArgumentException("Price date cannot be in the future");
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
