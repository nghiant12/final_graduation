package org.example.final_graduation.services;

import org.example.final_graduation.entities.Account;
import org.example.final_graduation.entities.Order;
import org.example.final_graduation.repositories.AccountRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void createNewOrder() {

        Order newOrder = new Order();
        newOrder.setAdmin(accountRepository.findByID(4));
        newOrder.setCreatedDate(LocalDateTime.now());
        newOrder.setTotalPrice(BigDecimal.ZERO);
        newOrder.setType("At the counter");
        newOrder.setStatus("Processing"); // Set default values if necessary
        newOrder.setAddress("At the counter"); // Set default values if necessary
        Order ordera = orderRepository.save(newOrder);
        orderRepository.save(ordera);
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

    public List<Account> searchCustomers(String email) {
        return accountRepository.findCustomersByEmail(email);
    }

    public void deleteOrderById(Integer id) {
        orderRepository.deleteById(id);
    }

    // Thêm khách hàng vào đơn hàng
    public void addCustomerToOrder(Integer orderId, Integer customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Account customer = accountRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        order.setUser(customer); // Thiết lập khách hàng cho đơn hàng
        orderRepository.save(order); // Lưu lại thay đổi
    }
}
