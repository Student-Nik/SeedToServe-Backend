package com.seedtoserve.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seedtoserve.enums.OrderStatus;
import com.seedtoserve.model.Order;
import com.seedtoserve.model.Payment;
import com.seedtoserve.repository.OrderRepository;
import com.seedtoserve.repository.PaymentRepository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

@Service
public class PaymentVerificationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.seedtoserve.config.RazorpayConfig razorpayConfig;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public boolean verifyAndMarkPaid(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        // 1️ Verify signature
        String data = razorpayOrderId + "|" + razorpayPaymentId;
        String secret = razorpayConfig.getSecret();

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        String hash = Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));

        boolean isValid = hash.equals(razorpaySignature);

        if (!isValid) {
            System.out.println("Signature mismatch! Expected: " + hash + ", Received: " + razorpaySignature);
            return false;
        }

        // 2️ Update order status to PAID
        Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.PAID);  
        orderRepository.save(order);
        
     // 3️ Update payment table
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found"));

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
        payment.setStatus("PAID");  // Enum for PAID
        paymentRepository.save(payment);

        return true;
    }
}
