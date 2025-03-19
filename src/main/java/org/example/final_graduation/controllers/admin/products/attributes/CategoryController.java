package org.example.final_graduation.controllers.admin.products.attributes;

import org.example.final_graduation.entities.Category;
import org.example.final_graduation.repositories.products.attributes.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("")
    public String index(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category());// Truyền đối tượng mới vào model
        return "admin/attributes/categories/index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        boolean exists = categoryRepository.existsByName(category.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên danh mục sản phẩm đã tồn tại!");
            return "redirect:/admin/categories"; // Nếu trùng tên, quay lại trang danh sách
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("success", "Thêm danh mục sản phẩm thành công!");
        return "redirect:/admin/categories"; // Quay lại trang danh sách
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        // Kiểm tra đối tượng có tồn tại trong cơ sở dữ liệu hay không
        Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
        if (existingCategory == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kiểu dáng để cập nhật.");
            return "redirect:/admin/categories";
        }

        // Kiểm tra trùng tên (ngoại trừ chính nó)
        if (categoryRepository.existsByName(category.getName()) && !existingCategory.getName().equals(category.getName())) {
            redirectAttributes.addFlashAttribute("error", "Tên kiểu dáng đã tồn tại!");
            return "redirect:/admin/categories";
        }

        // Cập nhật dữ liệu
        existingCategory.setName(category.getName());
        categoryRepository.save(existingCategory);

        redirectAttributes.addFlashAttribute("success", "Cập nhật kiểu dáng thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{idCategory}")
    public String editForm(@PathVariable("idCategory") Integer idCategory, Model model, RedirectAttributes redirectAttributes) {
        Category categoryEdit = categoryRepository.findByID(idCategory);

        if (categoryEdit == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kiểu dáng.");
            return "redirect:/admin/categories";
        }

        model.addAttribute("category", categoryEdit);
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/attributes/categories/index"; // Trả về trang chỉnh sửa
    }
}