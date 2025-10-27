package com.seedtoserve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.seedtoserve.dto.AddressDetailsDTO;
import com.seedtoserve.model.Customer;
import com.seedtoserve.service.AddressService;
import com.seedtoserve.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;

    // Create Address for logged-in customer
    @PostMapping("/create")
    public ResponseEntity<String> createAddress(@Valid @RequestBody AddressDetailsDTO addressDetailsDto) {
        Customer customer = customerService.getLoggedInCustomer();
        return addressService.createAddress(addressDetailsDto);
    }

    // Delete Address for logged-in customer
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAddress() {
        Customer customer = customerService.getLoggedInCustomer();
        return addressService.deleteAddress(customer.getId().intValue());
    }

    // Update Address for logged-in customer
    @PutMapping("/update")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody AddressDetailsDTO addressDetailsDto) {
        Customer customer = customerService.getLoggedInCustomer();
        return addressService.updateAddress(addressDetailsDto, customer.getId().intValue());
    }

    // Show Address for logged-in customer
    @GetMapping("/show")
    public ResponseEntity<?> showAddress() {
        Customer customer = customerService.getLoggedInCustomer();
        return addressService.showAddress(customer.getId().intValue());
    }
}
