package org.example.final_graduation.controllers.admin.products;

import org.example.final_graduation.entities.Product;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "admin/products/index";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("idProduct") Integer idProduct, Model model) {
        List<ProductDetail> productDetails = productDetailRepository.findByProductID(idProduct);
        model.addAttribute("productDetails", productDetails);
        return "admin/products/detail";
    }
}
