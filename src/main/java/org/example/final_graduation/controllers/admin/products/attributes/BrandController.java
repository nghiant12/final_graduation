package org.example.final_graduation.controllers.admin.products.attributes;

import org.example.final_graduation.entities.Brand;
import org.example.final_graduation.repositories.products.attributes.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("admin/brands")
public class BrandController {
    @Autowired
    private BrandRepository brandRepository;

    // Hiển thị trang danh sách thương hiệu và form
    @GetMapping("")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", "username");
        List<Brand> brands = brandRepository.findAll();
        model.addAttribute("brands", brands); // Truyền danh sách thương hiệu
        model.addAttribute("brand", new Brand()); // Truyền đối tượng trống cho form
        return "admin/attributes/brands/index";
    }

    // Thêm thương hiệu mới
    @PostMapping("/add")
    public String add(@ModelAttribute Brand brand, RedirectAttributes redirectAttributes) {
        boolean exists = brandRepository.existsByName(brand.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên thương hiệu đã tồn tại!");
            return "redirect:/admin/brands"; // Nếu trùng tên, quay lại trang danh sách
        }

        brandRepository.save(brand); // Lưu thương hiệu mới
        redirectAttributes.addFlashAttribute("success", "Thêm thương hiệu thành công!");
        return "redirect:/admin/brands"; // Quay lại trang danh sách
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute Brand brand, RedirectAttributes redirectAttributes) {
        boolean exists = brandRepository.existsByName(brand.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên thương hiệu đã tồn tại!");
            return "redirect:/admin/brands"; // Nếu trùng tên, quay lại trang danh sách
        }

        brandRepository.save(brand); // Lưu thương hiệu đã chỉnh sửa
        redirectAttributes.addFlashAttribute("success", "Chỉnh sửa thương hiệu thành công!");
        return "redirect:/admin/brands"; // Quay lại trang danh sách
    }

    @GetMapping("/edit/{idBrand}")
    public String editForm(@PathVariable("idBrand") Integer idBrand, Model model, RedirectAttributes redirectAttributes) {
        Brand brandEdit = brandRepository.findByID(idBrand);

        if (brandEdit == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thương hiệu.");
            return "redirect:/admin/brands";
        }

        // Truyền thương hiệu vào model để hiển thị trong form
        model.addAttribute("brand", brandEdit);
        List<Brand> brands = brandRepository.findAll();
        model.addAttribute("brands", brands); // Truyền danh sách thương hiệu vào để giữ nguyên table
        return "admin/attributes/brands/index"; // Trả về trang chỉnh sửa
    }

}
