package org.example.final_graduation.controllers;

import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.PromotionRepository;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.ProductRepository;
import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.services.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeCtrl {
    private final ProductDetailRepository productDetailRepository;
    private final CustomerService customerService;
    @Autowired
    private PromotionRepository promotionRepository;

    public HomeCtrl(ProductDetailRepository productDetailRepository, CustomerService customerService) {
        this.productDetailRepository = productDetailRepository;
        this.customerService = customerService;
    }

    @RequestMapping("/")
    public String index(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser");

        model.addAttribute("isAuthenticated", isAuthenticated);

        String username = principal.getName();
        model.addAttribute("username", username);

        List<ProductDetail> top4Product = productDetailRepository.findTop4Products(PageRequest.of(0, 4));
        model.addAttribute("top4Product", top4Product);

        List<ProductDetail> top4DacSac = productDetailRepository.findTop4DacSac(PageRequest.of(0, 4));
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
        return "/product/detail";
    }

    @RequestMapping("/admin")
    public String admin() {
        return "redirect:/admin/index.html";
    }

    @GetMapping("/my-profile")
    public String getProfile() {
        return "login/user";
    }

    @GetMapping("/info")
    public String getInfo() {
        return "layout/info";
    }

    @GetMapping("/thank-you")
    public String thankYou() {
        return "layout/thank-you";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser");

        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated && principal != null) {
            String username = principal.getName();
            Optional<Customer> customer = customerService.findByUsername(username);

            if (customer != null) {
                model.addAttribute("email", customer.get().getEmail());
                model.addAttribute("fullName", customer.get().getFullname());
                model.addAttribute("phone", customer.get().getPhoneNumber());
            } else {
                model.addAttribute("email", username);
                model.addAttribute("fullName", "");
                model.addAttribute("phone", "");
            }
        } else {
            model.addAttribute("email", "");
            model.addAttribute("fullName", "");
            model.addAttribute("phone", "");
        }
        model.addAttribute("promotions", promotionRepository.findAll());
        return "layout/checkout";
    }

}
