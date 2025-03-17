package org.example.final_graduation.dto;

import lombok.Data;
import org.example.final_graduation.entities.Order;

import java.math.BigDecimal;
import java.util.List;


@Data
public class OrderResponse {
    private Integer orderId;
    private String status;
    private BigDecimal totalPrice;
    private List<OrderDetailResponse> orderDetails;

    public OrderResponse(Integer orderId, String status, BigDecimal totalPrice, List<OrderDetailResponse> orderDetails) {
        this.orderId = orderId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderDetails = orderDetails;
    }

    public OrderResponse(Integer id, String status, BigDecimal totalPrice) {
        this.orderId = id;
        this.status = status;
        this.totalPrice = totalPrice;
    }
}