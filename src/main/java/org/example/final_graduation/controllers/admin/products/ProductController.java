package org.example.final_graduation.controllers.admin.products;

import org.example.final_graduation.entities.Product;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("admin/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @GetMapping("")
    public String index(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("product", new Product());
        return "admin/products/index";
    }

    @PostMapping("add")
    public String add(@ModelAttribute Product product, RedirectAttributes redirectAttributes){
        if (product.getStatus() == null) {
            product.setStatus(true); // Hoặc false nếu muốn mặc định là không hoạt động
        }
        boolean exists = productRepository.existsByName(product.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại!");
            return "redirect:/admin/products";
        }
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        return "redirect:/admin/products";
    }
    @PostMapping("/update")
    public String edit(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        // Kiểm tra tên sản phẩm, nhưng bỏ qua sản phẩm hiện tại
        boolean exists = productRepository.existsByName(product.getName()) &&
                !productRepository.findByID(product.getId()).getName().equals(product.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại!");
            return "redirect:/admin/products";
        }

        productRepository.save(product);
        redirectAttributes.addFlashAttribute("success", "Chỉnh sửa sản phẩm thành công!");
        return "redirect:/admin/products"; // Quay lại trang danh sách
    }

    @GetMapping("/edit/{idProduct}")
    public String editForm(@PathVariable("idProduct") Integer idProduct, Model model, RedirectAttributes redirectAttributes) {
        Product productEdit = productRepository.findByID(idProduct);

        if (productEdit == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", productEdit);
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "admin/attributes/products/index";
    }


    @GetMapping("/detail")
    public String detail(@RequestParam("idProduct") Integer idProduct, Model model) {
        List<ProductDetail> productDetails = productDetailRepository.findByProductID(idProduct);
        model.addAttribute("productDetails", productDetails);
        return "admin/products/detail";
    }
}
