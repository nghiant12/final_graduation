package org.example.final_graduation.configs;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordUpdateRunner implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordUpdateRunner(CustomerRepository customerRepository, EmployeeRepository employeeRepository) {
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        // Cập nhật mật khẩu cho Customers
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            if (!customer.getPassword().startsWith("$2a$")) { // Kiểm tra nếu chưa được mã hóa
                customer.setPassword(passwordEncoder.encode(customer.getPassword()));
                customerRepository.save(customer);
            }
        }

        // Cập nhật mật khẩu cho Employees
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            if (!employee.getPassword().startsWith("$2a$")) { // Kiểm tra nếu chưa được mã hóa
                employee.setPassword(passwordEncoder.encode(employee.getPassword()));
                employeeRepository.save(employee);
            }
        }

        System.out.println("🔐 Mật khẩu đã được cập nhật thành công!");
    }
}

