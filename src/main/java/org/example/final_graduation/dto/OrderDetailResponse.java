package org.example.final_graduation.dto;

import lombok.Data;


import lombok.Data;

@Data
public class OrderDetailResponse {
    private Long productDetailId;
    private Double price;
    private Integer quantity;

    public OrderDetailResponse(Long productDetailId, Double price, Integer quantity) {
        this.productDetailId = productDetailId;
        this.price = price;
        this.quantity = quantity;
    }
}
