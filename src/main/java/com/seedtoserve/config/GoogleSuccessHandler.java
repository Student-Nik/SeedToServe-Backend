package com.seedtoserve.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.seedtoserve.model.Customer;
import com.seedtoserve.repository.CustomerRepository;
import com.seedtoserve.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        Customer customer;
        if (customerOpt.isEmpty()) {
            
            customer = new Customer();
            customer.setEmail(email);
            customer.setFirstName(name);
            customer.setRegistrationType("BUYER");
            customerRepository.save(customer);
        } else {
            customer = customerOpt.get();
        }

        
        String token = jwtUtil.createToken(customer.getEmail());

       
        response.sendRedirect("http://localhost:5173/auth-success?token=" + token);
    }
}
