package org.example.final_graduation.controllers.admin.products.attributes;

import org.example.final_graduation.entities.Color;
import org.example.final_graduation.repositories.products.attributes.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("admin/colors")
public class ColorController {
    @Autowired
    private ColorRepository colorRepository;

    @GetMapping("")
    public String index(Model model) {
        List<Color> colors = colorRepository.findAll();
        model.addAttribute("colors", colors);
        model.addAttribute("color", new Color()); // Truyền đối tượng mới vào model
        return "admin/attributes/colors/index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Color color, RedirectAttributes redirectAttributes) {
        boolean exists = colorRepository.existsByName(color.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên màu sắc đã tồn tại!");
            return "redirect:/admin/colors"; // Nếu trùng tên, quay lại trang danh sách
        }

        colorRepository.save(color); // Lưu kích thước mới
        redirectAttributes.addFlashAttribute("success", "Thêm màu sắc thành công!");
        return "redirect:/admin/colors"; // Quay lại trang danh sách
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute Color color, RedirectAttributes redirectAttributes) {
        // Kiểm tra đối tượng có tồn tại trong cơ sở dữ liệu hay không
        Color existingColor = colorRepository.findById(color.getId()).orElse(null);
        if (existingColor == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy màu sắc để cập nhật.");
            return "redirect:/admin/colors";
        }

        // Kiểm tra trùng tên (ngoại trừ chính nó)
        if (colorRepository.existsByName(color.getName()) && !existingColor.getName().equals(color.getName())) {
            redirectAttributes.addFlashAttribute("error", "Tên màu sắc đã tồn tại!");
            return "redirect:/admin/colors";
        }

        // Cập nhật dữ liệu
        existingColor.setName(color.getName());
        colorRepository.save(existingColor);

        redirectAttributes.addFlashAttribute("success", "Cập nhật màu sắc thành công!");
        return "redirect:/admin/colors";
    }



    @GetMapping("/edit/{idColor}")
    public String editForm(@PathVariable("idColor") Integer idColor, Model model, RedirectAttributes redirectAttributes) {
        Color colorEdit = colorRepository.findByID(idColor);

        if (colorEdit == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy màu sắc.");
            return "redirect:/admin/colors";
        }

        // Truyền thương hiệu vào model để hiển thị trong form
        model.addAttribute("color", colorEdit);
        List<Color> colors = colorRepository.findAll();
        model.addAttribute("colors", colors);  // Truyền danh sách thương hiệu vào để giữ nguyên table
        return "admin/attributes/colors/index"; // Trả về trang chỉnh sửa
    }
}
