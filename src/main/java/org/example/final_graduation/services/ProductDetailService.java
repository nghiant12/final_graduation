package org.example.final_graduation.services;

import org.example.final_graduation.entities.Color;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.entities.Size;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.attributes.ColorRepository;
import org.example.final_graduation.repositories.products.attributes.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductDetailService {
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private SizeRepository sizeRepository;

    public List<ProductDetail> getAllProductDetails() {
        return productDetailRepository.findAll();
    }

    public List<ProductDetail> getProductDetailsByProductId(Integer productId) {
        return productDetailRepository.findByProductId(productId);
    }

    public Optional<ProductDetail> getProductDetailById(Integer id) {
        return productDetailRepository.findById(id);
    }

    public Optional<ProductDetail> getProductDetail(Integer productId, Integer brandId, Integer colorId, Integer sizeId) {
        return productDetailRepository.findByProductAndAttributes(productId, brandId, colorId, sizeId);
    }

    public ProductDetail saveProductDetail(ProductDetail productDetail) {
        return productDetailRepository.save(productDetail);
    }

    public Optional<Color> getColorById(Integer colorId) {
        return colorRepository.findById(colorId);
    }

    public Optional<Size> getSizeById(Integer sizeId) {
        return sizeRepository.findById(sizeId);
    }

    public ProductDetail findById(Integer id) {
        return productDetailRepository.findByID(id);
    }

}
