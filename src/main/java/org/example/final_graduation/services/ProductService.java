package org.example.final_graduation.services;

import org.example.final_graduation.entities.Product;
import org.example.final_graduation.repositories.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }
}
