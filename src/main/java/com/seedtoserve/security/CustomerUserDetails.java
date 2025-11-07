package com.seedtoserve.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.seedtoserve.model.Customer;

public class CustomerUserDetails implements UserDetails{

	private final Customer customer;

    public CustomerUserDetails(Customer customer) {
        this.customer = customer;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRegistrationType().toUpperCase()));
    }

    public String getPassword() {
        return customer.getPassword();
    }

    public String getUsername() {
        return customer.getEmail();
    }

    public boolean isAccountNonExpired() { return true; }

    public boolean isAccountNonLocked() { return true; }

    public boolean isCredentialsNonExpired() { return true; }

    public boolean isEnabled() { return true; }

    public Customer getCustomer() {
        return customer;
    }
    
    public Long getId() {
        return customer.getId();
    }


}
