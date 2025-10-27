package com.seedtoserve.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.seedtoserve.dto.AddressDetailsDTO;
import com.seedtoserve.model.AddressDetails;
import com.seedtoserve.model.Customer;
import com.seedtoserve.repository.AddressRepository;
import com.seedtoserve.repository.CustomerRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class AddressService {

	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	// Create Address
	
		public ResponseEntity<String> createAddress(AddressDetailsDTO addressDetailsDto){
			
			Optional<AddressDetails> isExistingMobileNo = addressRepository.findByMobileNo(addressDetailsDto.getMobileNo());
			
			if(isExistingMobileNo.isPresent()) {
				return ResponseEntity.status(HttpStatus.OK)
						.body("The address for "+addressDetailsDto.getMobileNo()+" is already exists!");
			}else {
				AddressDetails addressDetails = new AddressDetails();
				
				addressDetails.setFullName(addressDetailsDto.getFullName());
				addressDetails.setMobileNo(addressDetailsDto.getMobileNo());
				addressDetails.setHouseNoOrStreet(addressDetailsDto.getHouseNoOrStreet());
				addressDetails.setVillageOrTown(addressDetailsDto.getVillageOrTown());
				addressDetails.setDistrict(addressDetailsDto.getDistrict());
				addressDetails.setState(addressDetailsDto.getState());
				addressDetails.setPincode(addressDetailsDto.getPincode());
				
				Customer customer = customerRepository.findById((long) addressDetailsDto.getCustomerId())
		                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + addressDetailsDto.getCustomerId()));
				
				addressDetails.setCustomer(customer);
				
				addressRepository.save(addressDetails);
				
				return ResponseEntity.status(HttpStatus.CREATED)
						.body("Address saved successfully for Customer ID: " + addressDetailsDto.getCustomerId());
			}
		}
		
		// Delete Address
		
		@Transactional
		@Modifying
		public ResponseEntity<String> deleteAddress(int id){
			
			Optional<AddressDetails> isExistingAddress = addressRepository.findByCustomerId(id);
			
			if(isExistingAddress.isPresent()) {
				addressRepository.deleteById(id);
				return ResponseEntity.status(HttpStatus.ACCEPTED)
						.body("Address deleted Successfully!");
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Address not found!");
			}
		}
		
		// Update Address
		
		@Transactional
		public ResponseEntity<String> updateAddress(AddressDetailsDTO addressDetailsDto, int id) {
		    
		    Optional<AddressDetails> isExistingAddress = addressRepository.findByCustomerId(id);
		    if (isExistingAddress.isPresent()) {
		        AddressDetails addressDetails = isExistingAddress.get();

		        addressDetails.setFullName(addressDetailsDto.getFullName());
		        addressDetails.setMobileNo(addressDetailsDto.getMobileNo());
		        addressDetails.setHouseNoOrStreet(addressDetailsDto.getHouseNoOrStreet());
		        addressDetails.setVillageOrTown(addressDetailsDto.getVillageOrTown());
		        addressDetails.setDistrict(addressDetailsDto.getDistrict());
		        addressDetails.setState(addressDetailsDto.getState());
		        addressDetails.setPincode(addressDetailsDto.getPincode());

		        Customer customer = customerRepository.findById((long) addressDetailsDto.getCustomerId())
		                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + addressDetailsDto.getCustomerId()));
		        
		        addressDetails.setCustomer(customer);

		        addressRepository.save(addressDetails);

		        return ResponseEntity.status(HttpStatus.OK)
		                .body("Address Details Updated Successfully!");
		    }
		    return ResponseEntity.status(HttpStatus.NOT_FOUND)
		            .body("Address Details not found!");
		}
		
		// Show Address
		
		public ResponseEntity<?> showAddress(int id) {
		    Optional<AddressDetails> address = addressRepository.findByCustomerId(id);

		    if (address.isPresent()) {
		        return ResponseEntity.ok(address.get());
		    } else {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND)
		                .body("Address not found with ID: " + id);
		    }
		}

		


}
