package com.seedtoserve.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seedtoserve.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer>{

	Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
