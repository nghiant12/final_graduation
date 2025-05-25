package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class APIService {

    @Autowired
    private ProductDetailRepository productDetailRepository;

    public boolean updateStock(Integer productDetailId, int quantity) {
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(productDetailId);

        if (optionalProductDetail.isPresent()) {
            ProductDetail productDetail = optionalProductDetail.get();
            int currentStock = productDetail.getQuantity();

            if (currentStock >= quantity) {
                // Giảm số lượng tồn kho
                productDetail.setQuantity(currentStock - quantity);
                productDetailRepository.save(productDetail);
                return true;  // Cập nhật thành công
            } else {
                return false;  // Không đủ tồn kho
            }
        }
        return false;  // Không tìm thấy sản phẩm
    }
}