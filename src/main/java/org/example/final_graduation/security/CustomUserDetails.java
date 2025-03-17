package org.example.final_graduation.security;

import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.entities.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // Constructor cho Employee
    public CustomUserDetails(Employee employee) {
        this.username = employee.getUsername();
        this.password = employee.getPassword();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(employee.getRole().getName()));
    }

    // Constructor cho Customer
    public CustomUserDetails(Customer customer) {
        this.username = customer.getUsername();
        this.password = customer.getPassword();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")); // Gán quyền mặc định
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
