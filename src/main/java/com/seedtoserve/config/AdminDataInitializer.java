package com.seedtoserve.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.seedtoserve.model.Admin;
import com.seedtoserve.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner{

	private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Admin username and password creation
	@Override
	public void run(String... args) throws Exception {
		
		if (adminRepository.count() == 0) {

            Admin admin = new Admin();

            admin.setEmail("seedtoservewebapplication@gmail.com");
            admin.setPassword(
                passwordEncoder.encode("Admin@123")
            );
            admin.setStatus("ACTIVE");

            adminRepository.save(admin);
        }
	}
    
}
