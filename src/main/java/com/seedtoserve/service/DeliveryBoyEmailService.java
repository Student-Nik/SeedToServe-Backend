package com.seedtoserve.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.seedtoserve.model.AddressDetails;
import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.model.Order;
import com.seedtoserve.model.OrderItem;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryBoyEmailService {

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	public void sendDeliveryBoyCredentials(String email, String firstName, String temporaryPassword) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(email);
		message.setSubject("Welcome to SeedToServe - Delivery Boy Account");

		message.setText("Hello " + firstName + ",\n\n" + "Congratulations!\n\n"
				+ "You have been registered as a Delivery Boy for SeedToServe.\n\n"
				+ "You can now log in to your Delivery Boy account using the credentials below:\n\n" + "Email: " + email
				+ "\n" + "Temporary Password: " + temporaryPassword + "\n\n"
				+ "Please change your password after your first login.\n\n" + "Thank you,\n" + "SeedToServe Team");

		mailSender.send(message);
	}
	
	// Send order details to delivery boy
	public void sendOrderAssignmentEmail(DeliveryBoy deliveryBoy, Order order) {

	    AddressDetails address = order.getAddressDetails();
	    String orderDate = order.getOrderDate()
	            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

	    // Build product rows
	    StringBuilder productRows = new StringBuilder();
	    for (OrderItem item : order.getOrderItems()) {
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
	                </tr>
	                """.formatted(
	                item.getProduct().getName(),
	                item.getQuantity(),
	                item.getPrice()
	        ));
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

	                    <!-- MAIN CONTENT -->
	                    <div style="padding:30px;">
	                        <h2 style="
	                            margin-top:0;
	                            color:#166534;
	                        ">
	                            New Delivery Assigned 🚚
	                        </h2>

	                        <p style="font-size:16px;">
	                            Hello <strong>%s</strong>,
	                        </p>

	                        <p style="
	                            font-size:15px;
	                            line-height:1.6;
	                            color:#4b5563;
	                        ">
	                            A new order has been assigned to you for delivery.
	                            Please review the order and delivery address
	                            details below.
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
	                                        Payment Status
	                                    </td>
	                                    <td style="
	                                        padding:7px 0;
	                                        text-align:right;
	                                        font-weight:bold;
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
	                            🛒 Products
	                        </h3>

	                        <div style="
	                            border:1px solid #e5e7eb;
	                            border-radius:10px;
	                            overflow:hidden;
	                            margin-bottom:25px;
	                        ">
	                            <table width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;">
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

	                        <!-- ACTION MESSAGE -->
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
	                                🚚 Please deliver the order to the above address
	                                and handle the package carefully.
	                            </p>
	                        </div>

	                        <p style="
	                            font-size:14px;
	                            color:#6b7280;
	                            line-height:1.6;
	                        ">
	                            Thank you for being a part of the SeedToServe
	                            delivery network and helping us connect farmers
	                            with customers.
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
	            """.formatted(
	            deliveryBoy.getFirstName(),
	            order.getId(),
	            orderDate,
	            order.getPaymentMethod(),
	            order.getPaymentStatus(),
	            order.getOrderStatus(),
	            order.getTotalAmount(),
	            productRows.toString(),
	            address.getFullName(),
	            address.getMobileNo(),
	            address.getHouseNoOrStreet(),
	            address.getVillageOrTown(),
	            address.getDistrict(),
	            address.getState(),
	            address.getPincode()
	    );

	    // Create MIME message
	    MimeMessage message = mailSender.createMimeMessage();
	    try {
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	        helper.setFrom(fromEmail);
	        helper.setTo(deliveryBoy.getEmail());
	        helper.setSubject("🚚 New Delivery Assigned - Order #" + order.getId());
	        helper.setText(emailBody, true);
	        mailSender.send(message);
	        System.out.println("Delivery assignment email sent successfully to " + deliveryBoy.getEmail());
	    } catch (MessagingException e) {
	        throw new RuntimeException("Failed to send delivery assignment email", e);
	    }
	}
}
