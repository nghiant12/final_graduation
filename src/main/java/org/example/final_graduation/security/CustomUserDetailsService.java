package org.example.final_graduation.security;

import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

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
        System.out.println("Đang kiểm tra đăng nhập cho: " + username);

        Employee employee = employeeRepository.findByUsername(username).orElse(null);
        if (employee != null) {
            System.out.println("Tìm thấy Employee: " + username + " với role: " + employee.getRole().getName());
            String roleName = employee.getRole().getName().toUpperCase();
            return new User(employee.getUsername(), employee.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName)));
        }

        Customer customer = customerRepository.findByUsername(username).orElse(null);
        if (customer != null) {
            System.out.println("Tìm thấy Customer: " + username);
            return new User(customer.getUsername(), customer.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        System.out.println("Không tìm thấy người dùng: " + username);
        throw new UsernameNotFoundException("User not found");
    }

}
