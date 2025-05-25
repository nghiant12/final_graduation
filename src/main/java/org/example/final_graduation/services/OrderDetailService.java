package org.example.final_graduation.services;

import org.example.final_graduation.entities.Order;
import org.example.final_graduation.entities.OrderDetail;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Transactional
    public void addProductToOrder(Integer orderId, Integer productDetailId, Integer quantity) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(productDetailId);

        if (optionalOrder.isPresent() && optionalProductDetail.isPresent()) {
            Order order = optionalOrder.get();
            ProductDetail productDetail = optionalProductDetail.get();

            // Tìm sản phẩm trong orderDetail
            Optional<OrderDetail> existingOrderDetail = orderDetailRepository
                    .findByOrderAndProductDetail(order, productDetail);

            BigDecimal priceIncrease = productDetail.getPrice().multiply(BigDecimal.valueOf(quantity));

            if (existingOrderDetail.isPresent()) {
                // Nếu sản phẩm đã có trong đơn hàng, tăng số lượng
                OrderDetail orderDetail = existingOrderDetail.get();
                orderDetail.setQuantity(orderDetail.getQuantity() + quantity);
                orderDetail.setPrice(orderDetail.getPrice().add(priceIncrease));
                orderDetailRepository.save(orderDetail);
            } else {
                // Nếu sản phẩm chưa có, tạo mới
                OrderDetail newOrderDetail = new OrderDetail();
                newOrderDetail.setOrder(order);
                newOrderDetail.setProductDetail(productDetail);
                newOrderDetail.setQuantity(quantity);
                newOrderDetail.setPrice(priceIncrease);
                orderDetailRepository.save(newOrderDetail);
            }

            // Cập nhật tổng giá trị đơn hàng
            order.setTotalPrice(order.getTotalPrice().add(priceIncrease));
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Order hoặc ProductDetail không tồn tại!");
        }
    }

}
