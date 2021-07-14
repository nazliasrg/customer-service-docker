package com.microservices.customer.service;

import com.microservices.customer.model.entity.CustomerEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomerDetailsImpl implements UserDetails {
    private String username;
    private String password;

    public CustomerDetailsImpl(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static CustomerDetailsImpl build(CustomerEntity customer){
        return new CustomerDetailsImpl(customer.getUsername(), customer.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
