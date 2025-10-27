package com.seedtoserve.service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.CartItemDTO;
import com.seedtoserve.dto.CartItemResponse;
import com.seedtoserve.dto.CartSummaryDTO;
import com.seedtoserve.model.CartItem;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Product;
import com.seedtoserve.repository.CartItemRepository;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.repository.ProductRepository;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    //  Add a product into cart for a logged-in customer
    public ResponseEntity<String> addCart(CartItemDTO cartItemDto, Long customerId) {

        // Check customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Check product exists
        Product product = productRepository.findById(cartItemDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists in cart
        Optional<CartItem> existing = cartItemRepository.findByCustomerAndProduct(customer, product);

        if (existing.isPresent()) {
            CartItem cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDto.getQuantity());
            cartItemRepository.save(cartItem);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Cart updated successfully!");
        } else {
            // Validate quantity
            if (cartItemDto.getQuantity() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Quantity must be greater than 0");
            }
            if (cartItemDto.getQuantity() > product.getStock()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Only " + product.getStock() + " units available in stock");
            }

            CartItem cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemDto.getQuantity());
            cartItemRepository.save(cartItem);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Cart item added successfully!");
        }
    }

    //  Fetch Products from Cart
    public CartSummaryDTO getCartByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<CartItemResponse> items = cartItems.stream().map(item -> {
            Product product = item.getProduct();
            String imageBase64 = null;

            if (product.getImage() != null && product.getImage().length > 0) {
                imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(product.getImage());
            }

            return new CartItemResponse(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    item.getQuantity(),
                    imageBase64,
                    product.getPrice() * item.getQuantity()
            );
        }).toList();

        double totalAmount = items.stream().mapToDouble(CartItemResponse::getSubtotal).sum();
        int totalItems = items.stream().mapToInt(CartItemResponse::getQuantity).sum();

        return new CartSummaryDTO(items, totalAmount, totalItems);
    }

    //  Clear Cart
    public ResponseEntity<String> clearCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No cart found for this customer!");
        }

        cartItemRepository.deleteAll(cartItems);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Entire cart cleared for customer ID: " + customerId);
    }

    //  Delete Single Item from Cart
    public ResponseEntity<String> deleteItemFromCart(String itemId, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Optional<CartItem> existingCart = cartItemRepository.findById(itemId);
        if (existingCart.isPresent() && existingCart.get().getCustomer().equals(customer)) {
            cartItemRepository.deleteById(itemId);
            return ResponseEntity.ok("Cart item deleted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cart item not found for this customer!");
        }
    }

    //  Update Cart Item Quantity
    public ResponseEntity<String> updateCartItemQuantity(String itemId, int quantity, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Ensure that item belongs to logged-in customer
        if (!cartItem.getCustomer().equals(customer)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot modify another customer's cart item!");
        }

        if (quantity <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Quantity must be greater than 0");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return ResponseEntity.ok("Cart item quantity updated successfully!");
    }
}
