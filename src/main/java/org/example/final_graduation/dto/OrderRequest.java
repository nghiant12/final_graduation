package org.example.final_graduation.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderRequest {
    private Integer customerId;
    private Integer employeeId;
    private String address;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private String type;
    private String status;
    private List<OrderDetailRequest> orderDetails;

    // Constructor không tham số
    public OrderRequest() {}

    // Constructor có tham số
    public OrderRequest(Integer customerId, Integer employeeId, String address, String paymentMethod,
                        BigDecimal totalPrice, String type, String status, List<OrderDetailRequest> orderDetails) {
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.type = type;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    // Getters và Setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderDetailRequest> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailRequest> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
