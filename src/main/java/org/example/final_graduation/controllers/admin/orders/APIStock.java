package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class APIStock {
    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    APIService apiService;

    @GetMapping("/api/products/{productDetailId}/stock")
    public Integer getStock(@PathVariable Integer productDetailId) {
        Integer stock = productDetailRepository.getStockByProductId(productDetailId);
        if (stock != null) {
            return stock;
        } else {
            return null;
        }
    }

    @PostMapping("/api/products/{productDetailId}/updateStock")
    public ResponseEntity<String> updateStock(@PathVariable Integer productDetailId, @RequestBody Map<String, Integer> request) {
        int quantity = request.get("quantity");

        Optional<ProductDetail> optional = productDetailRepository.findById(productDetailId);

        if (optional.isPresent()) {
            ProductDetail detail = optional.get();
            if (detail.getQuantity() < quantity) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Không đủ hàng trong kho");
            }
            detail.setQuantity(detail.getQuantity() - quantity);
            productDetailRepository.save(detail);
            return ResponseEntity.ok("Đã cập nhật số lượng tồn kho");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm");
    }
}
