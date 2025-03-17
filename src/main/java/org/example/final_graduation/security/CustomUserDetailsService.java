package org.example.final_graduation.security;

import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository, CustomerRepository customerRepository) {
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsername(username).orElse(null);
        if (employee != null) {
            return new CustomUserDetails(employee);
        }

        Customer customer = customerRepository.findByUsername(username).orElse(null);
        if (customer != null) {
            return new CustomUserDetails(customer);
        }

        throw new UsernameNotFoundException("User not found");
    }
}
