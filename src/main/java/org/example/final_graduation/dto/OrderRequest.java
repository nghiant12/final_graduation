package org.example.final_graduation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderRequest {
    private Integer customerId;
    private Integer employeeId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createDate;

    private String address;
    private Integer promotionId;
    private String promotionCode;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private String type;
    private String status;
    private List<OrderDetailRequest> orderDetails;
    private BigDecimal subTotal; // Tiền đơn hàng trước giảm giá và ship
    private BigDecimal shippingFee; // Tiền ship
    private BigDecimal discountAmount; // Số tiền giảm giá

    // Constructor không tham số
    public OrderRequest() {}

    // Constructor có tham số
    public OrderRequest(Integer customerId, Integer employeeId, LocalDateTime createDate, String address, Integer promotionId, String promotionCode, String paymentMethod,
                        BigDecimal totalPrice, String type, String status, List<OrderDetailRequest> orderDetails) {
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.createDate = createDate;
        this.address = address;
        this.promotionId = promotionId;
        this.promotionCode = promotionCode;
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

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
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
}