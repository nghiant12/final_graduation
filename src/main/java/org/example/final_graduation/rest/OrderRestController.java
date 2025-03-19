package org.example.final_graduation.rest;

import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest/orders")
public class OrderRestController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @PostMapping
    public ResponseEntity<?> saveOrder(@RequestBody Map<String, Object> data) {
        try {
            // Lấy thông tin khách hàng
            String username = data.get("account") != null ? ((Map<String, Object>) data.get("account")).get("username").toString() : null;
            Optional<Customer> customerOpt = customerRepository.findByUsername(username);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Khách hàng không tồn tại!"));
            }
            Customer customer = customerOpt.get();

            // Lấy thông tin đơn hàng
            String address = data.get("address").toString();
            String phoneNumber = data.get("phoneNumber").toString();
            boolean pay = Boolean.parseBoolean(data.get("pay").toString());
            String status = data.get("status").toString();
            BigDecimal totalAmount = new BigDecimal(data.get("totalAmount").toString());
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) data.get("orderDetails");

            // Tạo đơn hàng mới
            Order order = new Order();
            order.setCustomer(customer);
            order.setCreatedDate(LocalDateTime.now());
            order.setTotalPrice(totalAmount);
            order.setType("Online");
            order.setStatus(status);
            order.setAddress(address);
            order.setPaymentMethod(pay ? "VNPay" : "Thanh toán khi nhận hàng");

            orderRepository.save(order);

            // Lưu danh sách sản phẩm trong đơn hàng
            for (Map<String, Object> item : cartItems) {
                Integer productDetailId = Integer.parseInt(((Map<String, Object>) item.get("product")).get("id").toString());
                Optional<ProductDetail> productDetailOpt = productDetailRepository.findById(productDetailId);
                if (productDetailOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Sản phẩm không tồn tại!"));
                }

                ProductDetail productDetail = productDetailOpt.get();
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProductDetail(productDetail);
                orderDetail.setPrice(productDetail.getPrice());
                orderDetail.setQuantity(Integer.parseInt(item.get("quantity").toString()));

                orderDetailRepository.save(orderDetail);
            }

            return ResponseEntity.ok(Map.of("message", "Đơn hàng đã được lưu thành công!", "orderId", order.getId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi khi lưu đơn hàng: " + e.getMessage()));
        }
    }
}

