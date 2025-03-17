package org.example.final_graduation.dto;

import java.math.BigDecimal;

public class OrderDetailRequest {
    private Integer productDetailId;
    private BigDecimal price;
    private Integer quantity;

    // Constructor không tham số
    public OrderDetailRequest() {}

    // Constructor có tham số
    public OrderDetailRequest(Integer productDetailId, BigDecimal price, Integer quantity) {
        this.productDetailId = productDetailId;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public Integer getProductDetailId() {
        return productDetailId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // Setters
    public void setProductDetailId(Integer productDetailId) {
        this.productDetailId = productDetailId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
