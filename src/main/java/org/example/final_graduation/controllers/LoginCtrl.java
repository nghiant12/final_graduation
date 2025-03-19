package org.example.final_graduation.controllers;

import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.entities.Role;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.example.final_graduation.repositories.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginCtrl {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginCtrl(EmployeeRepository employeeRepository, CustomerRepository customerRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("message", "Invalid username or password.");
        }
        model.addAttribute("account", new Customer());
        return "login/loginform-d";
    }

    @PostMapping("/signup/customer")
    public String registerCustomer(@ModelAttribute("account") Customer customer,
                                   @RequestParam("confirmPassword") String confirmPassword,
                                   Model model) {
        if (customerRepository.findByUsername(customer.getUsername()).isPresent()) {
            model.addAttribute("messageSignup", "Username already exists!");
            return "login/loginform-d";
        }
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            model.addAttribute("messageSignup", "Email already in use!");
            return "login/loginform-d";
        }
        if (!customer.getPassword().equals(confirmPassword)) {
            model.addAttribute("messageSignup", "Passwords do not match!");
            return "login/loginform-d";
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
        model.addAttribute("messageSignup", "Sign up successful! You can now log in.");
        return "login/loginform-d";
    }
}
