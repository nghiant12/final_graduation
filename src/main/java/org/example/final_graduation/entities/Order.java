package org.example.final_graduation.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private Account user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private Account admin;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "address", nullable = false)
    private String address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    public void removeOrderDetail(OrderDetail orderDetail) {
        orderDetails.remove(orderDetail);
        orderDetail.setOrder(null);
    }

    public Order() {
    }

    public Order(Integer id, Account user, Account admin, LocalDateTime createdDate, BigDecimal totalPrice, String type, String status, String address, List<OrderDetail> orderDetails) {
        this.id = id;
        this.user = user;
        this.admin = admin;
        this.createdDate = createdDate;
        this.totalPrice = totalPrice;
        this.type = type;
        this.status = status;
        this.address = address;
//        this.orderDetails = orderDetails;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public @NotNull Account getAdmin() {
        return admin;
    }

    public void setAdmin(@NotNull Account admin) {
        this.admin = admin;
    }

    public @NotNull LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@NotNull LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public @NotNull BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(@NotNull BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public @Size(max = 50) @NotNull String getType() {
        return type;
    }

    public void setType(@Size(max = 50) @NotNull String type) {
        this.type = type;
    }

    public @Size(max = 50) @NotNull String getStatus() {
        return status;
    }

    public void setStatus(@Size(max = 50) @NotNull String status) {
        this.status = status;
    }

    public @Size(max = 255) @NotNull String getAddress() {
        return address;
    }

    public void setAddress(@Size(max = 255) @NotNull String address) {
        this.address = address;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}