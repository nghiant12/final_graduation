package org.example.final_graduation.controllers.admin.products.attributes;

import org.example.final_graduation.entities.Brand;
import org.example.final_graduation.repositories.products.attributes.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("admin/brands")
public class BrandRestController {
    @Autowired
    private BrandRepository brandRepository;

    @PostMapping("/addr")
    public ResponseEntity<Brand> addr(@RequestBody Brand brand, Model model) {
        Brand brandAdd = brandRepository.save(brand);
        return ResponseEntity.ok(brandAdd);
    }

//    @GetMapping("/edit/{idBrand}")
//    public ResponseEntity<Brand> editForm(@PathVariable("idBrand") Integer idBrand, Model model) {
//        Brand brandEdit = brandRepository.findByID(idBrand);
//        if (brandEdit == null) {
//            model.addAttribute("error", "Không tìm thấy thương hiệu.");
//            return ResponseEntity.badRequest().build();
//        }
//
//        System.out.println("aaaaaaa" + brandEdit.getName());
//
//        model.addAttribute("brandEdit", brandEdit); // Đảm bảo brandEdit được truyền vào model
//        return ResponseEntity.ok(brandEdit);
//    }
}
