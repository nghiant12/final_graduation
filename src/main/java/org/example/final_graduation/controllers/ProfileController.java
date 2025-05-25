package org.example.final_graduation.controllers;

import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.services.CustomerService;
import org.example.final_graduation.services.EmployeeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final EmployeeService employeeService;
    private final CustomerService customerService;

    public ProfileController(EmployeeService employeeService, CustomerService customerService) {
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        String username = authentication.getName();

        var employeeOpt = employeeService.findByUsername(username);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            employee.setPassword(null);
            model.addAttribute("user", employee);
            model.addAttribute("userType", "employee");
        } else {
            var customerOpt = customerService.findByUsername(username);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                customer.setPassword(null);
                model.addAttribute("user", customer);
                model.addAttribute("userType", "customer");
            } else {
                // Không tìm thấy user thì redirect login hoặc báo lỗi
                return "redirect:/my-profile";
            }
        }

        return "/login/user"; // trả về Thymeleaf view
    }

    @PostMapping("/employee")
    public String updateEmployeeProfile(@ModelAttribute("user") Employee empUpdate,
                                        BindingResult result,
                                        Authentication authentication,
                                        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", empUpdate);
            model.addAttribute("userType", "employee");
            return "/login/user";
        }

        String username = authentication.getName();
        if (!username.equals(empUpdate.getUsername())) {
            model.addAttribute("error", "Không được phép cập nhật người khác");
            model.addAttribute("user", empUpdate);
            model.addAttribute("userType", "employee");
            return "/login/user";
        }
        Employee updated = employeeService.updateProfile(empUpdate);
        updated.setPassword(null);
        model.addAttribute("user", updated);
        model.addAttribute("userType", "employee");
        model.addAttribute("success", "Cập nhật thành công!");
        return "/login/user";
    }

    @PostMapping("/customer")
    public String updateCusProfile(@ModelAttribute("user") Customer cusUpdate,
                                        BindingResult result,
                                        Authentication authentication,
                                        Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", cusUpdate);
            model.addAttribute("userType", "customer");
            return "/login/user";
        }

        String username = authentication.getName();
        if (!username.equals(cusUpdate.getUsername())) {
            model.addAttribute("error", "Không được phép cập nhật người khác");
            model.addAttribute("user", cusUpdate);
            model.addAttribute("userType", "customer");
            return "/login/user";
        }
        Customer updated = customerService.updateProfile(cusUpdate);
        updated.setPassword(null);
        model.addAttribute("user", updated);
        model.addAttribute("userType", "customer");
        model.addAttribute("success", "Cập nhật thành công!");
        return "/login/user";
    }

}
