package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.entities.Order;
import org.example.final_graduation.entities.OrderDetail;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("admin/order-manager")
public class OrderManagerController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        List<Order> orders = orderRepository.findAllOrderManager();
        model.addAttribute("orders", orders);
        return "admin/order_manager/index";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam(value = "startDate", required = false) String startDate,
                         @RequestParam(value = "endDate", required = false) String endDate,
                         @RequestParam(value = "type", required = false) String type,
                         Model model) {
        // Xử lý ngày (tránh lỗi nếu người dùng chưa chọn)
        LocalDateTime start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate).atStartOfDay() : null;
        LocalDateTime end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate).atTime(23, 59, 59) : null;

        // Tìm kiếm hóa đơn theo ngày + loại
        List<Order> filteredOrders;
        if (start != null && end != null) {
            filteredOrders = (type == null || type.isEmpty())
                    ? orderRepository.findByCreatedDateBetween(start, end)
                    : orderRepository.findByCreatedDateBetweenAndType(start, end, type);
        } else {
            filteredOrders = (type == null || type.isEmpty())
                    ? orderRepository.findAllOrderManager()
                    : orderRepository.findByType(type);
        }

        // Lưu dữ liệu vào model để hiển thị lại trên giao diện
        model.addAttribute("orders", filteredOrders);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("type", type);

        return "admin/order_manager/index";
    }


    //
//    @GetMapping("/edit/{idProduct}")
//    public String editForm(@PathVariable("idProduct") Integer idProduct, Model model, RedirectAttributes redirectAttributes) {
//        Product productEdit = productRepository.findByID(idProduct);
//
//        if (productEdit == null) {
//            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
//            return "redirect:/admin/products";
//        }
//
//        model.addAttribute("product", productEdit);
//        List<Product> products = productRepository.findAll();
//        model.addAttribute("products", products);
//        return "admin/attributes/products/index";
//    }
//
//
    @GetMapping("/detail")
    public String detail(@RequestParam("idOrder") Integer idOrder, Model model,  Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(idOrder);
        model.addAttribute("order", orderRepository.findByID(idOrder));
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("totalPrice", orderDetailRepository.tongTien(idOrder));
        return "admin/order_manager/detail";
    }
}
