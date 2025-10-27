package com.seedtoserve.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seedtoserve.model.AddressDetails;

@Repository
public interface AddressRepository extends JpaRepository<AddressDetails, Integer>{

	Optional<AddressDetails> findByMobileNo(String mobileNo);
	
	Optional<AddressDetails> findByCustomerId(int id);
}
