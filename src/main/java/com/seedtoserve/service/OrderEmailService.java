package com.seedtoserve.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.seedtoserve.model.AddressDetails;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Order;
import com.seedtoserve.model.OrderItem;
import com.seedtoserve.model.Payment;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderEmailService {

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	// Order Confirmation Service
	public void sendOrderConfirmationEmail(Customer customer, Order order) {

		AddressDetails address = order.getAddressDetails();

		String orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

		// Build product rows
		StringBuilder productRows = new StringBuilder();

		for (OrderItem item : order.getOrderItems()) {

			double itemTotal = item.getPrice() * item.getQuantity();

			productRows.append("""
					<tr>
					    <td style="padding:12px 10px;border-bottom:1px solid #e5e7eb;">
					        <strong>%s</strong>
					    </td>

					    <td style="padding:12px 10px;text-align:center;border-bottom:1px solid #e5e7eb;">
					        %d
					    </td>

					    <td style="padding:12px 10px;text-align:right;border-bottom:1px solid #e5e7eb;">
					        ₹%.2f
					    </td>

					    <td style="padding:12px 10px;text-align:right;border-bottom:1px solid #e5e7eb;">
					        ₹%.2f
					    </td>
					</tr>
					""".formatted(item.getProduct().getName(), item.getQuantity(), item.getPrice(), itemTotal));
		}

		String emailBody = """
				<!DOCTYPE html>
				<html>

				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				</head>

				<body style="
				    margin:0;
				    padding:0;
				    background-color:#f3f4f6;
				    font-family:Arial,Helvetica,sans-serif;
				    color:#1f2937;
				">

				    <div style="
				        max-width:700px;
				        margin:30px auto;
				        background:#ffffff;
				        border-radius:14px;
				        overflow:hidden;
				        box-shadow:0 4px 15px rgba(0,0,0,0.08);
				    ">

				        <!-- HEADER -->
				        <div style="
				            background:linear-gradient(135deg,#15803d,#22c55e);
				            padding:28px 30px;
				            color:white;
				        ">

				            <div style="
				                font-size:26px;
				                font-weight:bold;
				                margin-bottom:6px;
				            ">
				                🌱 SeedToServe
				            </div>

				            <div style="
				                font-size:14px;
				                opacity:0.95;
				            ">
				                Farmer to Customer • Fresh • Direct
				            </div>

				        </div>


				        <!-- CONTENT -->
				        <div style="padding:30px;">

				            <h2 style="
				                margin-top:0;
				                color:#166534;
				            ">
				                🎉 Order Confirmed!
				            </h2>

				            <p style="font-size:16px;">
				                Hello <strong>%s</strong>,
				            </p>

				            <p style="
				                font-size:15px;
				                line-height:1.6;
				                color:#4b5563;
				            ">
				                Thank you for shopping with SeedToServe.
				                Your order has been successfully placed.
				                Below are your order details.
				            </p>


				            <!-- ORDER SUMMARY -->
				            <div style="
				                background:#f0fdf4;
				                border:1px solid #bbf7d0;
				                border-radius:10px;
				                padding:20px;
				                margin:25px 0;
				            ">

				                <h3 style="
				                    margin-top:0;
				                    color:#166534;
				                ">
				                    📦 Order Summary
				                </h3>

				                <table width="100%%" cellpadding="0" cellspacing="0">

				                    <tr>
				                        <td style="padding:7px 0;color:#6b7280;">
				                            Order ID
				                        </td>

				                        <td style="
				                            padding:7px 0;
				                            text-align:right;
				                            font-weight:bold;
				                        ">
				                            #%d
				                        </td>
				                    </tr>

				                    <tr>
				                        <td style="padding:7px 0;color:#6b7280;">
				                            Order Date
				                        </td>

				                        <td style="
				                            padding:7px 0;
				                            text-align:right;
				                        ">
				                            %s
				                        </td>
				                    </tr>

				                    <tr>
				                        <td style="padding:7px 0;color:#6b7280;">
				                            Payment Method
				                        </td>

				                        <td style="
				                            padding:7px 0;
				                            text-align:right;
				                        ">
				                            %s
				                        </td>
				                    </tr>



				                    <tr>
				                        <td style="padding:7px 0;color:#6b7280;">
				                            Order Status
				                        </td>

				                        <td style="
				                            padding:7px 0;
				                            text-align:right;
				                            font-weight:bold;
				                            color:#166534;
				                        ">
				                            %s
				                        </td>
				                    </tr>

				                    <tr>
				                        <td style="
				                            padding:12px 0 5px;
				                            color:#374151;
				                            font-weight:bold;
				                        ">
				                            Total Amount
				                        </td>

				                        <td style="
				                            padding:12px 0 5px;
				                            text-align:right;
				                            font-size:20px;
				                            font-weight:bold;
				                            color:#15803d;
				                        ">
				                            ₹%.2f
				                        </td>
				                    </tr>

				                </table>

				            </div>


				            <!-- PRODUCTS -->
				            <h3 style="
				                color:#166534;
				                margin-bottom:12px;
				            ">
				                🛒 Ordered Products
				            </h3>

				            <div style="
				                border:1px solid #e5e7eb;
				                border-radius:10px;
				                overflow:hidden;
				                margin-bottom:25px;
				            ">

				                <table width="100%%"
				                       cellpadding="0"
				                       cellspacing="0"
				                       style="border-collapse:collapse;">

				                    <thead>

				                        <tr style="background:#f9fafb;">

				                            <th style="
				                                padding:12px 10px;
				                                text-align:left;
				                                color:#4b5563;
				                                font-size:13px;
				                            ">
				                                Product
				                            </th>

				                            <th style="
				                                padding:12px 10px;
				                                text-align:center;
				                                color:#4b5563;
				                                font-size:13px;
				                            ">
				                                Quantity
				                            </th>

				                            <th style="
				                                padding:12px 10px;
				                                text-align:right;
				                                color:#4b5563;
				                                font-size:13px;
				                            ">
				                                Price
				                            </th>

				                            <th style="
				                                padding:12px 10px;
				                                text-align:right;
				                                color:#4b5563;
				                                font-size:13px;
				                            ">
				                                Total
				                            </th>

				                        </tr>

				                    </thead>

				                    <tbody>

				                        %s

				                    </tbody>

				                </table>

				            </div>


				            <!-- DELIVERY ADDRESS -->
				            <div style="
				                background:#fff7ed;
				                border:1px solid #fed7aa;
				                border-radius:10px;
				                padding:20px;
				                margin-bottom:25px;
				            ">

				                <h3 style="
				                    margin-top:0;
				                    color:#c2410c;
				                ">
				                    📍 Delivery Address
				                </h3>

				                <p style="
				                    margin:7px 0;
				                    font-size:15px;
				                ">
				                    <strong>%s</strong>
				                </p>

				                <p style="
				                    margin:7px 0;
				                    color:#4b5563;
				                ">
				                    📞 %s
				                </p>

				                <p style="
				                    margin:7px 0;
				                    color:#4b5563;
				                    line-height:1.6;
				                ">
				                    %s<br>
				                    %s<br>
				                    %s, %s<br>
				                    PIN - %s
				                </p>

				            </div>


				            <!-- STATUS MESSAGE -->
				            <div style="
				                background:#ecfdf5;
				                border-left:4px solid #16a34a;
				                padding:15px 18px;
				                border-radius:6px;
				                margin-bottom:25px;
				            ">

				                <p style="
				                    margin:0;
				                    color:#166534;
				                    font-size:14px;
				                    line-height:1.6;
				                ">
				                    ✅ Your order has been received successfully.
				                    We will notify you when your order is assigned
				                    for delivery.
				                </p>

				            </div>


				            <p style="
				                font-size:14px;
				                color:#6b7280;
				                line-height:1.6;
				            ">
				                Thank you for choosing SeedToServe and supporting
				                farmers by buying fresh products directly.
				            </p>

				        </div>


				        <!-- FOOTER -->
				        <div style="
				            background:#f9fafb;
				            border-top:1px solid #e5e7eb;
				            padding:20px 30px;
				            text-align:center;
				        ">

				            <div style="
				                font-weight:bold;
				                color:#166534;
				                margin-bottom:5px;
				            ">
				                🌱 SeedToServe Team
				            </div>

				            <div style="
				                font-size:12px;
				                color:#9ca3af;
				            ">
				                Empowering Farmers • Connecting Communities
				            </div>

				        </div>

				    </div>

				</body>
				</html>
				""".formatted(customer.getFirstName(), order.getId(), orderDate, order.getPaymentMethod(), order.getOrderStatus(), order.getTotalAmount(), productRows.toString(),
				address.getFullName(), address.getMobileNo(), address.getHouseNoOrStreet(), address.getVillageOrTown(),
				address.getDistrict(), address.getState(), address.getPincode());

		// Create MIME message
		MimeMessage message = javaMailSender.createMimeMessage();

		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(customer.getEmail());

			helper.setSubject("🎉 Order Confirmed - Order #" + order.getId());

			helper.setText(emailBody, true);

			javaMailSender.send(message);

			System.out.println("Order confirmation email sent successfully to " + customer.getEmail());

		} catch (MessagingException e) {

			throw new RuntimeException("Failed to send order confirmation email", e);
		}
	}

	// Payment Confirmation
	public void sendPaymentConfirmationEmail(Customer customer, Order order, Payment payment) {

		String orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

		String emailBody = """
				<!DOCTYPE html>
				<html>

				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				</head>

				<body style="
				    margin:0;
				    padding:0;
				    background-color:#f3f4f6;
				    font-family:Arial,Helvetica,sans-serif;
				    color:#1f2937;
				">

				    <div style="
				        max-width:650px;
				        margin:30px auto;
				        background:#ffffff;
				        border-radius:14px;
				        overflow:hidden;
				        box-shadow:0 4px 15px rgba(0,0,0,0.08);
				    ">

				        <!-- HEADER -->
				        <div style="
				            background:linear-gradient(135deg,#15803d,#22c55e);
				            padding:28px 30px;
				            color:white;
				        ">

				            <div style="
				                font-size:26px;
				                font-weight:bold;
				                margin-bottom:6px;
				            ">
				                🌱 SeedToServe
				            </div>

				            <div style="
				                font-size:14px;
				                opacity:0.95;
				            ">
				                Farmer to Customer • Fresh • Direct
				            </div>

				        </div>


				        <!-- MAIN CONTENT -->
				        <div style="padding:30px;">

				            <div style="
				                text-align:center;
				                padding:10px 0 20px;
				            ">
				                <div style="
				                    font-size:50px;
				                    margin-bottom:10px;
				                ">
				                    ✅
				                </div>
				                <h2 style="
				                    margin:0;
				                    color:#166534;
				                ">
				                    Payment Successful!
				                </h2>
				                <p style="
				                    color:#6b7280;
				                    font-size:15px;
				                    margin-top:8px;
				                ">
				                    Your payment has been successfully received.
				                </p>
				            </div>

				            <p style="font-size:16px;">
				                Hello <strong>%s</strong>,
				            </p>

				            <p style="
				                font-size:15px;
				                line-height:1.6;
				                color:#4b5563;
				            ">
				                Thank you for your payment. Your order has been
				                successfully paid for and is now being processed.
				            </p>


				            <!-- PAYMENT DETAILS -->
				            <div style="
				                background:#f0fdf4;
				                border:1px solid #bbf7d0;
				                border-radius:10px;
				                padding:20px;
				                margin:25px 0;
				            ">

				                <h3 style="
				                    margin-top:0;
				                    color:#166534;
				                ">
				                    💳 Payment Details
				                </h3>

				                <table width="100%%" cellpadding="0" cellspacing="0">

				                    <tr>
				                        <td style="
				                            padding:8px 0;
				                            color:#6b7280;
				                        ">
				                            Order ID
				                        </td>
				                        <td style="
				                            padding:8px 0;
				                            text-align:right;
				                            font-weight:bold;
				                        ">
				                            #%d
				                        </td>
				                    </tr>

				                    <tr>
				                        <td style="
				                            padding:8px 0;
				                            color:#6b7280;
				                        ">
				                            Payment ID
				                        </td>
				                        <td style="
				                            padding:8px 0;
				                            text-align:right;
				                            font-size:13px;
				                            word-break:break-all;
				                        ">
				                            %s
				                        </td>
				                    </tr>

				                    <tr>
				                        <td style="
				                            padding:8px 0;
				                            color:#6b7280;
				                        ">
				                            Payment Date
				                        </td>
				                        <td style="
				                            padding:8px 0;
				                            text-align:right;
				                        ">
				                            %s
				                        </td>
				                    </tr>

				                    <tr>
				                        <td style="
				                            padding:8px 0;
				                            color:#6b7280;
				                        ">
				                            Payment Method
				                        </td>
				                        <td style="
				                            padding:8px 0;
				                            text-align:right;
				                        ">
				                            %s
				                        </td>
				                    </tr>

				                    <tr>
				                        <td style="
				                            padding:12px 0 5px;
				                            color:#374151;
				                            font-weight:bold;
				                        ">
				                            Amount Paid
				                        </td>
				                        <td style="
				                            padding:12px 0 5px;
				                            text-align:right;
				                            font-size:20px;
				                            font-weight:bold;
				                            color:#15803d;
				                        ">
				                            ₹%.2f
				                        </td>
				                    </tr>

				                </table>

				            </div>


				            <!-- SUCCESS MESSAGE -->
				            <div style="
				                background:#ecfdf5;
				                border-left:4px solid #16a34a;
				                padding:15px 18px;
				                border-radius:6px;
				                margin-bottom:25px;
				            ">

				                <p style="
				                    margin:0;
				                    color:#166534;
				                    font-size:14px;
				                    line-height:1.6;
				                ">
				                    🎉 Your payment has been confirmed successfully.
				                    We will notify you when your order is assigned
				                    for delivery.
				                </p>

				            </div>

				            <p style="
				                font-size:14px;
				                color:#6b7280;
				                line-height:1.6;
				            ">
				                Thank you for choosing SeedToServe and supporting
				                farmers by buying fresh products directly.
				            </p>

				        </div>


				        <!-- FOOTER -->
				        <div style="
				            background:#f9fafb;
				            border-top:1px solid #e5e7eb;
				            padding:20px 30px;
				            text-align:center;
				        ">

				            <div style="
				                font-weight:bold;
				                color:#166534;
				                margin-bottom:5px;
				            ">
				                🌱 SeedToServe Team
				            </div>

				            <div style="
				                font-size:12px;
				                color:#9ca3af;
				            ">
				                Empowering Farmers • Connecting Communities
				            </div>

				        </div>

				    </div>

				</body>
				</html>
				""".formatted(customer.getFirstName(), order.getId(), payment.getRazorpayPaymentId(), orderDate,
				order.getPaymentMethod(), order.getTotalAmount());

		// Create MIME message
		MimeMessage message = javaMailSender.createMimeMessage();

		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(customer.getEmail());

			helper.setSubject("✅ Payment Successful - Order #" + order.getId());

			helper.setText(emailBody, true);

			javaMailSender.send(message);

			System.out.println("Payment confirmation email sent successfully to " + customer.getEmail());

		} catch (MessagingException e) {

			throw new RuntimeException("Failed to send payment confirmation email", e);
		}
	}
}
