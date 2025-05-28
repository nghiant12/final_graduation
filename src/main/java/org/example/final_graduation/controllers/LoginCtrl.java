package org.example.final_graduation.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.entities.Employee;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginCtrl {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginCtrl(EmployeeRepository employeeRepository, CustomerRepository customerRepository,
                     PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model,
                                @RequestParam(value = "showSignupForm", required = false) Boolean showSignupForm) {
        model.addAttribute("account", new Customer()); // cần thiết để tránh lỗi khi truy cập `th:object`
        model.addAttribute("showSignupForm", showSignupForm != null && showSignupForm);
        return "login/loginform-d";
    }

    @GetMapping
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("message", "Invalid username or password.");
        }
        if (!model.containsAttribute("account")) {
            model.addAttribute("account", new Customer());
        }
        return "login/loginform-d";
    }

    @PostMapping("/signin")
    public String signIn(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        // Kiểm tra nếu là Employee (Admin)
        Optional<Employee> employee = employeeRepository.findByUsername(username);
        if (employee.isPresent() && passwordEncoder.matches(password, employee.get().getPassword())) {
            session.setAttribute("role", "ADMIN");
            session.setAttribute("loggedInUser", employee.get().getUsername());
            session.setAttribute("customerId", employee.get().getId());
            redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công!");
            return "admin/orders/order_form";
        }

        // Kiểm tra nếu là Customer (User)
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent() && passwordEncoder.matches(password, customer.get().getPassword())) {
            session.setAttribute("customerId", customer.get().getId());
            session.setAttribute("loggedInUser", customer.get().getFullname());
            session.setAttribute("role", "USER");
            redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công!");
            return "redirect:/";
        }

        // Nếu không tìm thấy
        redirectAttributes.addFlashAttribute("message", "Invalid username or password.");
        return "redirect:/login";
    }

    @GetMapping("/signout")
    public String signOut(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); // Xóa toàn bộ session
        redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công.");
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("account", new Customer());
        return "login/loginform-d";
    }
}
