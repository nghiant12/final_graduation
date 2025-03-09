package org.example.final_graduation.controllers;

import org.example.final_graduation.entities.Product;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.ProductRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeCtrl {
    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;

    public HomeCtrl(ProductDetailRepository productDetailRepository, ProductRepository productRepository) {
        this.productDetailRepository = productDetailRepository;
        this.productRepository = productRepository;
    }

    @RequestMapping("/")
    public String index(Model model) {
        List<Product> top4Product = productRepository.findTopProducts(PageRequest.of(0, 4));
        for (Product product : top4Product) {
            Optional<ProductDetail> latestDetail = productDetailRepository.findLatestProductDetailByProductId(product.getId());
            latestDetail.ifPresent(product::setLatestProductDetail); // Lưu `ProductDetail` mới nhất vào `Product`
        }
        model.addAttribute("top4Product", top4Product);

        List<Product> top4DacSac = productRepository.findTopProductsByTotalQuantity(PageRequest.of(0, 4));
        for (Product product : top4DacSac) {
            Optional<ProductDetail> latestDetail = productDetailRepository.findLatestProductDetailByProductId(product.getId());
            latestDetail.ifPresent(product::setLatestProductDetail); // Lưu `ProductDetail` mới nhất vào `Product`
        }
        model.addAttribute("top4DacSac", top4DacSac);

        return "layout/index";
    }

    @RequestMapping("/about")
    public String about() {
        return "layout/about";
    }

    @RequestMapping("/contact")
    public String contact() {
        return "layout/contact";
    }

    @RequestMapping("/product")
    public String product() {
        return "layout/product";
    }

    @RequestMapping("/admin")
    public String admin() {
        return "redirect:/admin/index.html";
    }

    @GetMapping("/my-profile")
    public String getProfile() {
        return "login/user";
    }

    @GetMapping("info")
    public String getInfo() {
        return "layout/info";
    }

}
