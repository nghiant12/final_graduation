package org.example.final_graduation.controllers.user;

import org.example.final_graduation.entities.Order;
import org.example.final_graduation.entities.OrderDetail;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/order")
public class UserOrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("/list")
    public String listOrders(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String username = principal.getName();
        List<Order> orders = orderRepository.findByCustomerUsername(username);
        
        model.addAttribute("orders", orders);
        return "user/order/list";
    }

    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable("orderId") Integer orderId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        // Get order and verify it belongs to the current user
        Order order = orderRepository.findByID(orderId);
        if (order == null || !order.getCustomer().getUsername().equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn hàng này");
            return "redirect:/order/list";
        }

        // Get order details
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(orderId);
        
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("totalPrice", orderDetailRepository.tongTien(orderId));
        
        return "user/order/detail";
    }
} 