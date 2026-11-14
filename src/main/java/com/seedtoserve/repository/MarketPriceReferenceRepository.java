package com.seedtoserve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seedtoserve.model.MarketPriceReference;

@Repository
public interface MarketPriceReferenceRepository extends JpaRepository<MarketPriceReference, Long>{

	List<MarketPriceReference> findByProductNameIgnoreCaseAndDistrictIgnoreCaseAndStateIgnoreCase(
            String productName,
            String district,
            String state
    );
	
}
