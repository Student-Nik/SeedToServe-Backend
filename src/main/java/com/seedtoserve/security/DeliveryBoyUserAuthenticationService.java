package com.seedtoserve.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.seedtoserve.model.DeliveryBoy;
import com.seedtoserve.repository.DeliveryBoyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryBoyUserAuthenticationService implements UserDetailsService {

	private final DeliveryBoyRepository deliveryBoyRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		DeliveryBoy deliveryBoy = deliveryBoyRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Delivery boy not found with email: " + email));

		return User.builder().username(deliveryBoy.getEmail()).password(deliveryBoy.getPassword()).roles("DELIVERY_BOY")
				.build();
	}

}
