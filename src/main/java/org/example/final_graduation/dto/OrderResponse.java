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
    private BigDecimal subTotal;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;

    public OrderResponse(Integer orderId, String status, BigDecimal totalPrice, List<OrderDetailResponse> orderDetails, BigDecimal subTotal, BigDecimal shippingFee, BigDecimal discountAmount, BigDecimal finalTotal) {
        this.orderId = orderId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderDetails = orderDetails;
        this.subTotal = subTotal;
        this.shippingFee = shippingFee;
        this.discountAmount = discountAmount;
        this.finalTotal = finalTotal;
    }

    public OrderResponse(Integer id, String status, BigDecimal totalPrice) {
        this.orderId = id;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }
}