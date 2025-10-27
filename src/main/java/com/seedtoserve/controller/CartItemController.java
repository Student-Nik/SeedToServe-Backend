package com.seedtoserve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seedtoserve.dto.CartItemDTO;
import com.seedtoserve.dto.CartSummaryDTO;
import com.seedtoserve.model.Customer;
import com.seedtoserve.service.CartItemService;
import com.seedtoserve.service.CustomerService;

@RestController
@RequestMapping("/api/customer/cart")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CustomerService customerService;

    // Add item to cart for logged-in customer
    @PostMapping("/add")
    public ResponseEntity<String> addCart(@RequestBody CartItemDTO cartItemDto) {
        Customer customer = customerService.getLoggedInCustomer();
        return cartItemService.addCart(cartItemDto, customer.getId());
    }

    // Show cart for logged-in customer
    @GetMapping("/show")
    public CartSummaryDTO showCart() {
        Customer customer = customerService.getLoggedInCustomer();
        return cartItemService.getCartByCustomer(customer.getId());
    }

    // Clear cart for logged-in customer
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        Customer customer = customerService.getLoggedInCustomer();
        return cartItemService.clearCart(customer.getId());
    }

    // Delete an item from logged-in customer’s cart
    @DeleteMapping("/delete/item")
    public ResponseEntity<String> deleteCartItem(@RequestParam String itemId) {
        Customer customer = customerService.getLoggedInCustomer();
        return cartItemService.deleteItemFromCart(itemId, customer.getId());
    }

    // Update quantity of a specific item in logged-in customer’s cart
    @PutMapping("/update/item")
    public ResponseEntity<String> updateCartItemQuantity(
            @RequestParam String itemId,
            @RequestParam int quantity) {
        Customer customer = customerService.getLoggedInCustomer();
        return cartItemService.updateCartItemQuantity(itemId, quantity, customer.getId());
    }
}
