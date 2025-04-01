package org.example.final_graduation.services;

import org.example.final_graduation.dto.OrderDetailRequest;
import org.example.final_graduation.dto.OrderRequest;
import org.example.final_graduation.dto.OrderResponse;
import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void createNewOrder() {
        Order newOrder = new Order();
        newOrder.setEmployee(employeeRepository.findByID(4));
        newOrder.setCreatedDate(LocalDateTime.now());
        newOrder.setTotalPrice(BigDecimal.ZERO);
        newOrder.setType("At the counter");
        newOrder.setStatus("Processing"); // Set default values if necessary
        newOrder.setAddress("At the counter"); // Set default values if necessary
        Order order = orderRepository.save(newOrder);
        orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public void cancelOrder(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + id));
        order.setStatus("Cancelled");
        orderRepository.save(order);
    }

    public void deleteOrderById(Integer id) {
        orderRepository.deleteById(id);
    }

    // Thêm khách hàng vào đơn hàng
    public void addCustomerToOrder(Integer orderId, Integer customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        order.setCustomer(customer); // Thiết lập khách hàng cho đơn hàng
        orderRepository.save(order); // Lưu lại thay đổi
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Tạo order mới
        Order order = new Order();
        Customer c = customerRepository.findByID(orderRequest.getCustomerId());
        order.setCustomer(c);
        Employee e = employeeRepository.findByID(orderRequest.getEmployeeId());
        order.setEmployee(e);
        order.setAddress(orderRequest.getAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setType(orderRequest.getType());
        order.setStatus("Processing");
        order.setCreatedDate(LocalDateTime.now());
        order = orderRepository.save(order);

        // Lưu order details
        for (OrderDetailRequest detail : orderRequest.getOrderDetails()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            ProductDetail pd = productDetailRepository.findByID(detail.getProductDetailId());
            orderDetail.setProductDetail(pd);
            orderDetail.setPrice(detail.getPrice());
            orderDetail.setQuantity(detail.getQuantity());
            orderDetailRepository.save(orderDetail);
        }

        return new OrderResponse(order.getId(), order.getStatus(), order.getTotalPrice());
    }

    /**
     * Tìm đơn hàng theo ID
     */
    public Order findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
    }

    /**
     * Thêm một sản phẩm vào đơn hàng
     */
    @Transactional
    public void addProductToOrder(Order order, Integer productDetailId, int quantity) {
        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productDetailId));

        // Kiểm tra nếu sản phẩm đã có trong đơn hàng thì cập nhật số lượng
        Optional<OrderDetail> existingOrderDetail = orderDetailRepository.findByOrderAndProductDetail(order, productDetail);
        if (existingOrderDetail.isPresent()) {
            OrderDetail orderDetail = existingOrderDetail.get();
            orderDetail.setQuantity(orderDetail.getQuantity() + quantity);
            orderDetailRepository.save(orderDetail);
        } else {
            // Nếu sản phẩm chưa có trong đơn hàng, tạo mới
            OrderDetail newOrderDetail = new OrderDetail();
            newOrderDetail.setOrder(order);
            newOrderDetail.setProductDetail(productDetail);
            newOrderDetail.setQuantity(quantity);
            newOrderDetail.setPrice(productDetail.getPrice());

            orderDetailRepository.save(newOrderDetail);
        }
    }

    /**
     * Thêm nhiều sản phẩm vào đơn hàng
     */
    @Transactional
    public void addMultipleProductsToOrder(Integer orderId, List<Integer> productDetailIds) {
        Order order = findById(orderId);
        for (Integer productDetailId : productDetailIds) {
            addProductToOrder(order, productDetailId, 1); // Mặc định thêm 1 sản phẩm mỗi loại
        }
    }
}
