package org.example.final_graduation.controllers.admin;

import org.example.final_graduation.entities.Brand;
import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.products.attributes.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/customers")
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("")
    public String index(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers); // Truyền danh sách thương hiệu
        model.addAttribute("customer", new Customer()); // Truyền đối tượng trống cho form
        return "admin/customers/index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        boolean exists = customerRepository.existsByUsername(customer.getUsername());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên người dùng đã tồn tại!");
            return "redirect:/admin/customers";
        }
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("success", "Thêm khách hàng mới thành công!");
        return "redirect:/admin/customers"; // Quay lại trang danh sách
    }

    @PostMapping("/update")
    public String edit(Customer customer, RedirectAttributes redirectAttributes) {
//        boolean exists = customerRepository.existsByUsername(customer.getUsername());
//        if (exists) {
//            redirectAttributes.addFlashAttribute("error", "Tên khách hàng đã tồn tại!");
//            return "redirect:/admin/customers"; // Quay lại trang danh sách
//        }

        customerRepository.save(customer); // Lưu thương hiệu đã chỉnh sửa
        redirectAttributes.addFlashAttribute("success", "Chỉnh sửa khách hàng thành công!");
        return "redirect:/admin/customers"; // Quay lại trang danh sách
    }

    @GetMapping("/edit/{idCustomer}")
    public String editForm(@PathVariable("idCustomer") Integer idCustomer, Model model, RedirectAttributes redirectAttributes) {
        Customer customerEdit = customerRepository.findByID(idCustomer);

        if (customerEdit == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
            return "redirect:/admin/customers";
        }

        // Truyền thương hiệu vào model để hiển thị trong form
        model.addAttribute("customers", customerEdit);
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "admin/customers/index"; // Trả về trang chỉnh sửa
    }

}
