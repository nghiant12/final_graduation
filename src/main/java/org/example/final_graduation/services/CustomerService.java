package org.example.final_graduation.services;

import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Customer updateProfile(Customer customerUpdate) {
        return customerRepository.findByUsername(customerUpdate.getUsername())
                .map(existing -> {
                    existing.setFullname(customerUpdate.getFullname());
                    existing.setEmail(customerUpdate.getEmail());
                    existing.setPhoneNumber(customerUpdate.getPhoneNumber());
                    existing.setAddress(customerUpdate.getAddress());
                    if (customerUpdate.getPassword() != null && !customerUpdate.getPassword().isEmpty()) {
                        existing.setPassword(customerUpdate.getPassword());
                    }
                    return customerRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }
}

