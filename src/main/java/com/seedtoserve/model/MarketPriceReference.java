package com.seedtoserve.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "market_price_reference")
public class MarketPriceReference {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String marketName;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal modalPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxPrice;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private LocalDate priceDate;

    private String source;
}
