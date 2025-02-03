package org.example.final_graduation.controllers.admin.products;

import org.example.final_graduation.entities.Order;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.services.OrderService;
import org.example.final_graduation.services.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/product-details")
public class ProductDetailController {
    @Autowired
    private ProductDetailRepository productDetailRepository;



}