package org.example.final_graduation.controllers.admin.products.attributes;

import org.example.final_graduation.entities.Brand;
import org.example.final_graduation.entities.Size;
import org.example.final_graduation.repositories.products.attributes.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("admin/sizes")
public class SizeController {
    @Autowired
    private SizeRepository sizeRepository;

    // Hiển thị trang danh sách kích thước và form
    @GetMapping("")
    public String index(Model model) {
        List<Size> sizes = sizeRepository.findAll();
        model.addAttribute("sizes", sizes); // Truyền danh sách kích thước
        model.addAttribute("size", new Size()); // Truyền đối tượng trống cho form
        return "admin/attributes/sizes/index";
    }

    // Thêm kích thước mới
    @PostMapping("/add")
    public String add(@ModelAttribute Size size, RedirectAttributes redirectAttributes) {
        boolean exists = sizeRepository.existsByName(size.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên kích thước đã tồn tại!");
            return "redirect:/admin/sizes"; // Nếu trùng tên, quay lại trang danh sách
        }

        sizeRepository.save(size); // Lưu kích thước mới
        redirectAttributes.addFlashAttribute("success", "Thêm kích thước thành công!");
        return "redirect:/admin/sizes"; // Quay lại trang danh sách
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute Size size, RedirectAttributes redirectAttributes) {
        // Kiểm tra đối tượng có tồn tại trong cơ sở dữ liệu hay không
        Size existingSize = sizeRepository.findById(size.getId()).orElse(null);
        if (existingSize == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy kích thước để cập nhật.");
            return "redirect:/admin/sizes";
        }

        System.out.println("Received Size ID: " + size.getId());
        System.out.println("Received Size Name: " + size.getName());

        // Kiểm tra trùng tên (ngoại trừ chính nó)
        if (sizeRepository.existsByName(size.getName()) && !existingSize.getName().equals(size.getName())) {
            redirectAttributes.addFlashAttribute("error", "Tên kích thước đã tồn tại!");
            return "redirect:/admin/sizes";
        }

        // Cập nhật dữ liệu
        existingSize.setName(size.getName());
        sizeRepository.save(existingSize);

        redirectAttributes.addFlashAttribute("success", "Cập nhật kích thước thành công!");
        return "redirect:/admin/sizes";
    }



    @GetMapping("/edit/{idSize}")
    public String editForm(@PathVariable("idSize") Integer idSize, Model model, RedirectAttributes redirectAttributes) {
        Size sizeEdit = sizeRepository.findByID(idSize);

        if (sizeEdit == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thương hiệu.");
            return "redirect:/admin/sizes";
        }

        // Truyền thương hiệu vào model để hiển thị trong form
        model.addAttribute("size", sizeEdit);
        List<Size> sizes = sizeRepository.findAll();
        model.addAttribute("sizes", sizes);  // Truyền danh sách thương hiệu vào để giữ nguyên table
        return "admin/attributes/sizes/index"; // Trả về trang chỉnh sửa
    }
}
