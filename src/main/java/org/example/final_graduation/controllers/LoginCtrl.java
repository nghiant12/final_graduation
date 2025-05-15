//package org.example.final_graduation.controllers;
//
//import jakarta.servlet.http.HttpSession;
//import org.example.final_graduation.entities.Customer;
//import org.example.final_graduation.entities.Employee;
//import org.example.final_graduation.repositories.CustomerRepository;
//import org.example.final_graduation.repositories.EmployeeRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/login")
//public class LoginCtrl {
//
//    private final EmployeeRepository employeeRepository;
//    private final CustomerRepository customerRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public LoginCtrl(EmployeeRepository employeeRepository, CustomerRepository customerRepository,
//                     PasswordEncoder passwordEncoder) {
//        this.employeeRepository = employeeRepository;
//        this.customerRepository = customerRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @GetMapping
//    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
//        if (error != null) {
//            model.addAttribute("message", "Invalid username or password.");
//        }
//        return "login/loginform-d";
//    }
//
//    @PostMapping("/signin")
//    public String signIn(@RequestParam("username") String username,
//                         @RequestParam("password") String password,
//                         HttpSession session,
//                         RedirectAttributes redirectAttributes) {
//        // Kiểm tra nếu là Employee (Admin)
//        Optional<Employee> employee = employeeRepository.findByUsername(username);
//        if (employee.isPresent() && passwordEncoder.matches(password, employee.get().getPassword())) {
//            session.setAttribute("role", "ADMIN");
//            session.setAttribute("loggedInUser", employee.get().getUsername());
//            redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công!");
//            return "admin/orders/order_form";
//        }
//
//        // Kiểm tra nếu là Customer (User)
//        Optional<Customer> customer = customerRepository.findByUsername(username);
//        if (customer.isPresent() && passwordEncoder.matches(password, customer.get().getPassword())) {
//            session.setAttribute("loggedInUser", customer.get().getFullname());
//            session.setAttribute("role", "USER");
//            redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công!");
//            return "redirect:/";
//        }
//
//        // Nếu không tìm thấy
//        redirectAttributes.addFlashAttribute("message", "Invalid username or password.");
//        return "redirect:/login";
//    }
//
//    @GetMapping("/signout")
//    public String signOut(HttpSession session, RedirectAttributes redirectAttributes) {
//        session.invalidate(); // Xóa toàn bộ session
//        redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công.");
//        return "redirect:/login";
//    }
//
//    @PostMapping("/signup/customer")
//    public String registerCustomer(@ModelAttribute("account") Customer customer,
//                                   @RequestParam("confirmPassword") String confirmPassword,
//                                   RedirectAttributes redirectAttributes) {
//
//        // Kiểm tra username đã tồn tại chưa
//        if (customerRepository.findByUsername(customer.getUsername()).isPresent()) {
//            redirectAttributes.addFlashAttribute("messageSignup", "Username already exists!");
//            return "redirect:/login";
//        }
//
//        // Kiểm tra mật khẩu có trùng khớp không
//        if (!customer.getPassword().equals(confirmPassword)) {
//            redirectAttributes.addFlashAttribute("messageSignup", "Passwords do not match!");
//            return "redirect:/login";
//        }
//
//        // **Mã hóa mật khẩu trước khi lưu**
//        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
//        customerRepository.save(customer);
//
//        redirectAttributes.addFlashAttribute("messageSignup", "Sign up successful! You can now log in.");
//        return "redirect:/login";
//    }
//
//}
