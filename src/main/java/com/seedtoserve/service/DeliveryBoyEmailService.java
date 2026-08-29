package com.seedtoserve.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.seedtoserve.model.AddressDetails;
import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.model.Order;
import com.seedtoserve.model.OrderItem;

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

	// send Order details to delivery boy 
	public void sendOrderAssignmentEmail(DeliveryBoy deliveryBoy, Order order) {

		AddressDetails address = order.getAddressDetails();

		StringBuilder items = new StringBuilder();

		for (OrderItem item : order.getOrderItems()) {

			items.append("Product: ").append(item.getProduct().getName()).append("\n");

			items.append("Quantity: ").append(item.getQuantity()).append("\n");

			items.append("Price: ₹").append(item.getPrice()).append("\n\n");
		}

		String orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

		String emailBody = "Hello " + deliveryBoy.getFirstName() + ",\n\n"

				+ "You have been assigned a new delivery order " + "from SeedToServe.\n\n"

				+ "========== ORDER DETAILS ==========\n" + "Order ID: " + order.getId() + "\n" + "Order Date: "
				+ orderDate + "\n" + "Payment Method: " + order.getPaymentMethod() + "\n" + "Payment Status: "
				+ order.getPaymentStatus() + "\n" + "Order Status: " + order.getOrderStatus() + "\n" + "Total Amount: ₹"
				+ order.getTotalAmount() + "\n\n"

				+ "========== PRODUCTS ==========\n" + items

				+ "========== DELIVERY ADDRESS ==========\n" + "Name: " + address.getFullName() + "\n" + "Mobile: "
				+ address.getMobileNo() + "\n" + "House/Street: " + address.getHouseNoOrStreet() + "\n"
				+ "Village/Town: " + address.getVillageOrTown() + "\n" + "District: " + address.getDistrict() + "\n"
				+ "State: " + address.getState() + "\n" + "Pincode: " + address.getPincode() + "\n\n"

				+ "Please deliver the order to the above address.\n\n"

				+ "Regards,\n" + "SeedToServe Team";

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromEmail);
		message.setTo(deliveryBoy.getEmail());

		message.setSubject("New Delivery Assigned - Order #" + order.getId());

		message.setText(emailBody);

		mailSender.send(message);
	}
}
