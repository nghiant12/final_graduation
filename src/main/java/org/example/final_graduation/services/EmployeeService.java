package org.example.final_graduation.services;

import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    public Employee updateProfile(Employee employeeUpdate) {
        return employeeRepository.findByUsername(employeeUpdate.getUsername())
                .map(existing -> {
                    existing.setFullname(employeeUpdate.getFullname());
                    existing.setEmail(employeeUpdate.getEmail());
                    existing.setPhoneNumber(employeeUpdate.getPhoneNumber());
                    existing.setAddress(employeeUpdate.getAddress());
                    // Nếu update password (nên mã hóa trước khi lưu)
                    if (employeeUpdate.getPassword() != null && !employeeUpdate.getPassword().isEmpty()) {
                        existing.setPassword(employeeUpdate.getPassword());
                    }
                    return employeeRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }
}
