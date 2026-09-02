package com.seedtoserve.service;

import java.time.LocalDateTime;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Order;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyerOrderEmailService {

	private final JavaMailSender javaMailSender;

	public void sendOrderStatusEmail(Order order) {

		try {

			Customer customer = order.getCustomer();

			String customerName = customer.getFirstName() + " " + customer.getLastName();

			String email = customer.getEmail();

			String status = order.getOrderStatus().toString();

			String subject = "SeedToServe - Order #" + order.getId() + " " + getStatusMessage(status);

			String message = buildEmail(customerName, order, status);

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(message, true);

			javaMailSender.send(mimeMessage);

			System.out.println("Order status email sent successfully to " + email);

		} catch (Exception e) {

			System.out.println("Failed to send order status email: " + e.getMessage());
		}
	}

	private String getStatusMessage(String status) {

		switch (status) {

		case "ASSIGNED":
			return "has been assigned";

		case "SHIPPED":
			return "has been shipped";

		case "OUT_FOR_DELIVERY":
			return "is out for delivery";

		case "DELIVERED":
			return "has been delivered";

		default:
			return "status updated";
		}
	}

	private String buildEmail(String customerName, Order order, String status) {

		String statusMessage = getStatusMessage(status);

		return """
				<!DOCTYPE html>
				<html>
				<head>
				    <meta charset="UTF-8">
				    <style>

				        body {
				            font-family: Arial, sans-serif;
				            background-color: #f4f7f4;
				            margin: 0;
				            padding: 0;
				        }

				        .container {
				            max-width: 600px;
				            margin: 30px auto;
				            background: white;
				            border-radius: 12px;
				            overflow: hidden;
				            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
				        }

				        .header {
				            background-color: #2e7d32;
				            color: white;
				            padding: 25px;
				            text-align: center;
				        }

				        .header h1 {
				            margin: 0;
				            font-size: 28px;
				        }

				        .content {
				            padding: 30px;
				            color: #333;
				        }

				        .status {
				            background-color: #e8f5e9;
				            color: #2e7d32;
				            padding: 15px;
				            text-align: center;
				            border-radius: 8px;
				            font-size: 20px;
				            font-weight: bold;
				            margin: 20px 0;
				        }

				        .order-box {
				            background-color: #f8f9f8;
				            padding: 20px;
				            border-radius: 8px;
				            margin-top: 20px;
				        }

				        .footer {
				            background-color: #f1f1f1;
				            text-align: center;
				            padding: 20px;
				            font-size: 13px;
				            color: #777;
				        }

				    </style>
				</head>

				<body>

				    <div class="container">

				        <div class="header">
				            <h1>🌱 SeedToServe</h1>
				            <p>Farm Fresh. Delivered Fresh.</p>
				        </div>

				        <div class="content">

				            <h2>Hello %s 👋</h2>

				            <p>
				                Your order status has been updated.
				            </p>

				            <div class="status">
				                %s
				            </div>

				            <div class="order-box">

				                <p>
				                    <strong>Order ID:</strong> #%d
				                </p>

				                <p>
				                    <strong>Total Amount:</strong> ₹%.2f
				                </p>

				                <p>
				                    <strong>Payment:</strong> %s
				                </p>

				                <p>
				                    <strong>Updated On:</strong> %s
				                </p>

				            </div>

				            <p style="margin-top:25px;">
				                Thank you for choosing <strong>SeedToServe</strong>.
				                We are committed to bringing fresh products
				                from farmers directly to you.
				            </p>

				        </div>

				        <div class="footer">
				            © 2026 SeedToServe<br>
				            Connecting Farmers with Customers
				        </div>

				    </div>

				</body>
				</html>
				""".formatted(customerName, getStatusDisplayText(status), order.getId(), order.getTotalAmount(),
				order.getPaymentMethod(), LocalDateTime.now());
	}

	private String getStatusDisplayText(String status) {

		switch (status) {

		case "ASSIGNED":
			return "Your Order Has Been Assigned";

		case "SHIPPED":
			return "Your Order Has Been Shipped 🚚";

		case "OUT_FOR_DELIVERY":
			return "Your Order Is Out For Delivery 🛵";

		case "DELIVERED":
			return "Your Order Has Been Delivered 🎉";

		default:
			return "Order Status Updated";
		}
	}
}
