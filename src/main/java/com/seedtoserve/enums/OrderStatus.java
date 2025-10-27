package com.seedtoserve.enums;

public enum OrderStatus {
    PENDING,        // Order placed but payment not completed
    PAID,           // Payment completed successfully
    PROCESSING,     // Order confirmed, preparing for shipment
    SHIPPED,        // Handed over to courier
    DELIVERED,      // Customer received
    CANCELLED,      // Cancelled by user or admin
    FAILED          // Payment failed
}

