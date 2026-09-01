package com.seedtoserve.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.seedtoserve.model.Admin;
import com.seedtoserve.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserAuthenticationService implements UserDetailsService {

	private final AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Admin admin = adminRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));

		System.out.println("ADMIN FOUND: " + admin.getEmail());
		System.out.println("ADMIN PASSWORD HASH: " + admin.getPassword());
		System.out.println("ADMIN STATUS: " + admin.getStatus());

		return User.builder().username(admin.getEmail()).password(admin.getPassword()).roles("ADMIN").build();
	}
}