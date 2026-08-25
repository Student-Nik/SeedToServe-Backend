package com.seedtoserve.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private long totalBuyers;
    private long totalFarmers;
    private long totalProducts;
    private long totalOrders;

    private long pendingOrders;
    private long completedOrders;
    private long cancelledOrders;

    private BigDecimal totalRevenue;
}
