package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.dto.OrderDetailRequest;
import org.example.final_graduation.dto.OrderRequest;
import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.example.final_graduation.repositories.PromotionRepository;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class APIOrder {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @PostMapping
    public ResponseEntity<?> createOrderA(@RequestBody OrderRequest request) {
        Order order = new Order();

        // Lấy khách hàng
        Customer customer = customerRepository.findByID(request.getCustomerId());
        order.setCustomer(customer);

        Employee employee = null;
        order.setEmployee(employee);

        order.setCreatedDate(LocalDateTime.now());

        if (request.getTotalPrice() != null) {
            order.setTotalPrice(request.getTotalPrice());
        } else {
            return ResponseEntity.badRequest().body("Tổng tiền không được để trống");
        }

        order.setType(request.getType());
        order.setStatus(request.getStatus());
        order.setAddress(request.getAddress());

        if (request.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findByID(request.getPromotionId());
            order.setPromotion(promotion);
        }

        order.setPaymentMethod(request.getPaymentMethod());

        Order savedOrder = orderRepository.save(order);

        for (OrderDetailRequest detail : request.getOrderDetails()) {
            OrderDetail od = new OrderDetail();
            od.setOrder(savedOrder);

            ProductDetail productDetail = productDetailRepository.findByID(detail.getProductDetailId());
            od.setProductDetail(productDetail);
            od.setPrice(detail.getPrice());
            od.setQuantity(detail.getQuantity());
            orderDetailRepository.save(od);
        }

        return ResponseEntity.ok(Map.of("message", "Đơn hàng đã được tạo thành công!"));
    }

}
