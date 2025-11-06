package com.seedtoserve.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seedtoserve.config.RazorpayConfig;
import com.seedtoserve.model.Payment;
import com.seedtoserve.repository.PaymentRepository;
import com.seedtoserve.util.HmacSHA256Util;

@Service
public class PaymentVerificationService {

	@Autowired
	private RazorpayConfig razorpayConfig;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	// Method to verify Razorpay signature and update payment status
    public boolean verifyAndMarkPaid(String razorpayOrderId, String razorpayPaymentId, String signature) throws Exception {
        
        Optional<Payment> opt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (!opt.isPresent()) {
            return false; // order not found
        }

        Payment payment = opt.get();

        // 1️ Create payload in format: orderId|paymentId
        String payload = razorpayOrderId + "|" + razorpayPaymentId;

        // 2️ Generate server-side signature using secret key
        String generatedSignature = HmacSHA256Util.hmacSha256Hex(payload, razorpayConfig.getSecret());

        // 3️ Compare generated signature with Razorpay’s signature
        if (generatedSignature.equals(signature)) {
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setRazorpaySignature(signature);
            payment.setStatus("PAID");
            paymentRepository.save(payment);
            return true;
        } else {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            return false;
        }
    }
}

