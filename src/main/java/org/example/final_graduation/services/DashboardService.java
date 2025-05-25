package org.example.final_graduation.services;

import org.example.final_graduation.entities.Order;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public Map<String, Object> getDashboardStats(LocalDate start, LocalDate end) {
        LocalDateTime startDate = start.atStartOfDay();
        LocalDateTime endDate = end.atTime(LocalTime.MAX);
        List<Order> orders = orderRepository.findByCreatedDateBetween(startDate, endDate);

        // Tổng doanh thu (chỉ tính đơn hoàn thành)
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Số đơn hàng
        int totalOrders = orders.size();
        // Số đơn hoàn thành
        long completedOrders = orders.stream().filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus())).count();
        // Số đơn bị hủy
        long cancelledOrders = orders.stream().filter(o -> "CANCELLED".equalsIgnoreCase(o.getStatus())).count();
        // Số lượt dùng voucher
        long usedPromotions = orders.stream().filter(o -> o.getPromotion() != null).count();
        // Số khách hàng đăng ký mới
        long newCustomers = customerRepository.countRegisteredBetween(startDate, endDate);

        // Biểu đồ doanh thu theo ngày
        Map<String, BigDecimal> revenueByDay = new TreeMap<>();
        for (Order o : orders) {
            if ("COMPLETED".equalsIgnoreCase(o.getStatus())) {
                String day = o.getCreatedDate().toLocalDate().toString();
                revenueByDay.put(day, revenueByDay.getOrDefault(day, BigDecimal.ZERO).add(o.getTotalPrice()));
            }
        }
        // Biểu đồ trạng thái đơn hàng
        Map<String, Long> statusCount = orders.stream().collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

        // Danh sách đơn hàng (có thể giới hạn 50)
        List<Map<String, Object>> orderList = orders.stream().limit(50).map(o -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", o.getId());
            m.put("customer", o.getCustomer() != null ? o.getCustomer().getFullname() : "Khách lẻ");
            m.put("createdDate", o.getCreatedDate());
            m.put("totalPrice", o.getTotalPrice());
            m.put("status", o.getStatus());
            m.put("promotion", o.getPromotion() != null ? o.getPromotion().getCode() : null);
            m.put("promotionDiscount", o.getPromotion() != null ? o.getPromotion().getDiscount() : null);
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("totalOrders", totalOrders);
        result.put("completedOrders", completedOrders);
        result.put("cancelledOrders", cancelledOrders);
        result.put("usedPromotions", usedPromotions);
        result.put("newCustomers", newCustomers);
        result.put("revenueByDay", revenueByDay);
        result.put("statusCount", statusCount);
        result.put("orders", orderList);
        return result;
    }
} 