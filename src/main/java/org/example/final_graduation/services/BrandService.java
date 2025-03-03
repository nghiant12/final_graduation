package org.example.final_graduation.services;

import org.example.final_graduation.entities.Brand;
import org.example.final_graduation.repositories.products.attributes.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public List<Brand> getAvailableBrandsForProduct(Integer productId) {
        return brandRepository.findBrandsByProduct(productId);
    }

    public Optional<Brand> getBrandById(Integer brandId) {
        return brandRepository.findById(brandId);
    }
}
