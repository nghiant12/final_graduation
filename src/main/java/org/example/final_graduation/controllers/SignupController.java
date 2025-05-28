package org.example.final_graduation.controllers;
import jakarta.validation.Valid;
import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class SignupController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("account", new Customer());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String processSignup(
            @Valid @ModelAttribute("account") Customer customer,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("message", "Invalid data.");
            return "login/signup";
        }

        if (!customer.getPassword().equals(customer.getConfirmPassword())) {
            model.addAttribute("message", "The passwords determined do not match.");
            return "login/signup";
        }

        if (customerRepository.findByUsername(customer.getUsername()).isPresent()) {
            model.addAttribute("message", "The username already exists.");
            return "login/signup";
        }

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            model.addAttribute("message", "The email already exists.");
            return "login/signup";
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setCreatedDate(new Date());
        customer.setStatus(true);
        customerRepository.save(customer);

        model.addAttribute("message", "Registration successful!");
        model.addAttribute("account", new Customer()); // reset form
        return "login/signup";
    }
}

