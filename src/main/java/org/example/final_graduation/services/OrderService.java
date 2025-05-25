package org.example.final_graduation.services;

import org.example.final_graduation.dto.OrderDetailRequest;
import org.example.final_graduation.dto.OrderRequest;
import org.example.final_graduation.dto.OrderResponse;
import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.example.final_graduation.repositories.PromotionRepository;
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

    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionService promotionService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void createNewOrder() {
        Order newOrder = new Order();
        newOrder.setEmployee(employeeRepository.findByID(4));
        newOrder.setCreatedDate(LocalDateTime.now());
        newOrder.setSubTotal(BigDecimal.ZERO);
        newOrder.setShippingFee(BigDecimal.ZERO);
        newOrder.setDiscountAmount(BigDecimal.ZERO);
        newOrder.setTotalPrice(BigDecimal.ZERO);
        newOrder.setType("At the counter");
        newOrder.setStatus("PENDING_CONFIRMATION"); // Set default values if necessary
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

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        Customer c = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + orderRequest.getCustomerId()));
        order.setCustomer(c);

        if (orderRequest.getEmployeeId() != null) {
            Employee e = employeeRepository.findById(orderRequest.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + orderRequest.getEmployeeId()));
            order.setEmployee(e);
        } else {
            order.setEmployee(null);
        }

        order.setAddress(orderRequest.getAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setType(orderRequest.getType());
        order.setStatus("PENDING_CONFIRMATION");
        order.setCreatedDate(LocalDateTime.now());

        // Lấy breakdown từ OrderRequest
        BigDecimal subTotal = orderRequest.getSubTotal() != null ? orderRequest.getSubTotal() : BigDecimal.ZERO;
        BigDecimal shippingFee = orderRequest.getShippingFee() != null ? orderRequest.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discountAmount = orderRequest.getDiscountAmount() != null ? orderRequest.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal finalTotal = orderRequest.getTotalPrice() != null ? orderRequest.getTotalPrice() : subTotal.add(shippingFee).subtract(discountAmount);

        order.setSubTotal(subTotal);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTotalPrice(finalTotal);

        // Xử lý promotionCode (ưu tiên code, nếu không có thì dùng promotionId cũ)
        Promotion appliedPromotion = null;
        if (orderRequest.getPromotionCode() != null && !orderRequest.getPromotionCode().isEmpty()) {
            var promoOpt = promotionService.validatePromotionForUser(orderRequest.getPromotionCode(), subTotal);
            if (promoOpt.isPresent()) {
                appliedPromotion = promoOpt.get();
                // Trừ số lượng mã
                appliedPromotion.setRemainingQuantity(appliedPromotion.getRemainingQuantity() - 1);
                promotionRepository.save(appliedPromotion);
                order.setPromotion(appliedPromotion);
            }
        } else if (orderRequest.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(orderRequest.getPromotionId())
                    .orElse(null);
            order.setPromotion(promotion);
        }
        order.setTotalPrice(finalTotal);

        Order savedOrder = orderRepository.save(order);

        for (OrderDetailRequest detailRequest : orderRequest.getOrderDetails()) {
            ProductDetail productDetail = productDetailRepository.findById(detailRequest.getProductDetailId())
                    .orElseThrow(() -> new RuntimeException("ProductDetail not found with ID: " + detailRequest.getProductDetailId()));

            if (productDetail.getQuantity() < detailRequest.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + productDetail.getProduct().getName() + " không đủ số lượng. Tồn kho: " + productDetail.getQuantity() + ", Yêu cầu: " + detailRequest.getQuantity());
            }

            productDetail.setQuantity(productDetail.getQuantity() - detailRequest.getQuantity());
            productDetailRepository.save(productDetail);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setPrice(detailRequest.getPrice()); 
            orderDetail.setQuantity(detailRequest.getQuantity());
            orderDetailRepository.save(orderDetail);
        }

        // Trả về OrderResponse với breakdown
        return new OrderResponse(
            savedOrder.getId(),
            savedOrder.getStatus(),
            savedOrder.getTotalPrice(),
            null, // orderDetails có thể set ở nơi khác nếu cần
            subTotal,
            shippingFee,
            discountAmount,
            finalTotal
        );
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

    @Transactional
    public void updateOrderStatus(Integer orderId, String status, String vnpayResponseCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId + " để cập nhật trạng thái."));
        order.setStatus(status);
        // Nếu bạn muốn lưu vnpayResponseCode, bạn cần thêm một trường vào Order entity
        // Ví dụ: order.setVnpayResponseCode(vnpayResponseCode);
        orderRepository.save(order);
    }
}
