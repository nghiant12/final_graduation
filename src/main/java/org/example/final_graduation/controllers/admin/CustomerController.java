package org.example.final_graduation.controllers.admin;

import jakarta.validation.Valid;
import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/customers")
@SessionAttributes("customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        List<Customer> customers = customerRepository.findAll();
        if (!model.containsAttribute("customer")) {
            model.addAttribute("customer", new Customer());
        }
        model.addAttribute("customers", customers);
        return "admin/customers/index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("username") String username, Model model) {
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);

        if (customerOpt.isPresent()) {
            model.addAttribute("customers", List.of(customerOpt.get()));
        } else {
            model.addAttribute("customers", List.of());
            model.addAttribute("error", "Không tìm thấy khách hàng với username: " + username);
        }

        return "admin/customers/index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute @Valid Customer customer,
                      BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin!" + result.getFieldError());
            return "redirect:/admin/customers";
        }

        if (customerRepository.existsByUsername(customer.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Username đã tồn tại!");
            return "redirect:/admin/customers";
        }

        if (customerRepository.existsByEmail(customer.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
            return "redirect:/admin/customers";
        }

        try {
            Customer newCustomer = new Customer();
            newCustomer.setUsername(customer.getUsername());
            newCustomer.setEmail(customer.getEmail());
            newCustomer.setAddress(customer.getAddress());
            newCustomer.setFullname(customer.getFullname());
            newCustomer.setPhoneNumber(String.valueOf(customer.getPhoneNumber()));
            newCustomer.setStatus(true);
            newCustomer.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            customerRepository.save(newCustomer);
            redirectAttributes.addFlashAttribute("success", "Thêm khách hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi thêm khách hàng: " + e.getMessage());
        }

        return "redirect:/admin/customers";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customerOpt = customerRepository.findById(id);

        if (customerOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng!");
            return "redirect:/admin/customers";
        }

        Customer customer = customerOpt.get();
        customer.setUsername(customer.getUsername());
        customer.setFullname(customer.getFullname());
        customer.setEmail(customer.getEmail());
        customer.setAddress(customer.getAddress());
        customer.setPhoneNumber(customer.getPhoneNumber());
        customerRepository.save(customer);

        model.addAttribute("customer", customerOpt.get());
        return "admin/customers/index";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute @Valid Customer customer, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            return "redirect:/admin/customers";
        }

        Optional<Customer> existingCustomerOpt = customerRepository.findById(customer.getId());
        if (existingCustomerOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng!");
            return "redirect:/admin/customers";
        }

        Customer existingCustomer = existingCustomerOpt.get();

        // Kiểm tra username có bị thay đổi hay không
        if (!existingCustomer.getUsername().equals(customer.getUsername())) {
            if (customerRepository.existsByUsername(customer.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Username đã tồn tại!");
                return "redirect:/admin/customers";
            }
        }

        // Kiểm tra email có bị thay đổi hay không
        if (!existingCustomer.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(customer.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
                return "redirect:/admin/customers";
            }
        }

        try {
            // Giữ nguyên mật khẩu cũ khi cập nhật
            customer.setPassword(existingCustomer.getPassword());
            customer.setCreatedDate(existingCustomer.getCreatedDate()); // Không thay đổi createdDate
            customer.setStatus(existingCustomer.isStatus());
            customerRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khách hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi cập nhật khách hàng: " + e.getMessage());
        }

        return "redirect:/admin/customers";
    }

    @PostMapping("/activate/{id}")
    public String activateCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<Customer> optCustomer = customerRepository.findById(id);
        if (optCustomer.isPresent()) {
            Customer cus = optCustomer.get();
            cus.setStatus(true);
            customerRepository.save(cus);
            redirectAttributes.addFlashAttribute("success", "Đã kích hoạt khách hàng thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
        }
        return "redirect:/admin/customers";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<Customer> optCustomer = customerRepository.findById(id);
        if (optCustomer.isPresent()) {
            Customer cus = optCustomer.get();
            cus.setStatus(false);
            customerRepository.save(cus);
            redirectAttributes.addFlashAttribute("success", "Khách hàng đã ngừng hoạt động.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
        }
        return "redirect:/admin/customers";
    }
}