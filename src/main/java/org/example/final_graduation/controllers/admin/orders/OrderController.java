package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.AccountRepository;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.example.final_graduation.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private ColorService colorService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Hiển thị form tạo đơn hàng mới
    @GetMapping("")
    public String showCreateOrderForm(Model model) {
        Order order = new Order();
        // Giả sử bạn có người dùng đang đăng nhập (user)
        // Bạn có thể lấy từ session hoặc security context
        // Ví dụ đơn giản:
        Optional<Account> currentUser = accountService.getAccountById(1); // Giả sử user ID = 1
        currentUser.ifPresent(order::setUser);
        // Thiết lập admin nếu cần
        Optional<Account> adminUser = accountService.getAccountById(4); // Giả sử admin ID = 2
        adminUser.ifPresent(order::setAdmin);
        model.addAttribute("order", order);
        model.addAttribute("products", productDetailService.getAllProductDetails());

        //của Thịnh
        List<Order> orders = orderRepository.findAllProcessing();
        model.addAttribute("orders", orders);

        return "admin/orders/order_form";
    }

    // Xử lý tạo đơn hàng mới
    @PostMapping("/create")
    public String createOrder(RedirectAttributes redirectAttributes) {
        try {
            orderService.createNewOrder(); // Lưu hóa đơn
            redirectAttributes.addFlashAttribute("success", "Hóa đơn được tạo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể tạo hóa đơn!");
        }
        return "redirect:/admin/orders"; // Điều hướng lại trang danh sách hóa đơn
    }

    @GetMapping("/detail")
    public String showOrderDetail(@RequestParam Integer idOrder, Model model) {
        // Lấy danh sách hóa đơn
        List<Order> orders = orderRepository.findAllProcessing();
        model.addAttribute("orders", orders);

        // Lấy chi tiết hóa đơn dựa trên idOrder
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(idOrder);
        model.addAttribute("orderDetails", orderDetails);

        model.addAttribute("products", productDetailService.getAllProductDetails());

        // Gửi thêm thông tin nếu cần thiết
        Optional<Order> selectedOrder = orderRepository.findById(idOrder);
        selectedOrder.ifPresent(order -> model.addAttribute("order", order));

        return "admin/orders/order_form";
    }


    // Xử lý hủy đơn hàng
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(id); // Hủy hóa đơn
            redirectAttributes.addFlashAttribute("success", "Hóa đơn đã được hủy thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy hóa đơn!");
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/getProductDetails")
    @ResponseBody
    public Map<String, Object> getProductDetails(@RequestParam("productDetailId") Integer productDetailId) {
        Map<String, Object> response = new HashMap<>();
        Optional<ProductDetail> optionalProductDetail = productDetailService.getProductDetailById(productDetailId);

        if (optionalProductDetail.isPresent()) {
            ProductDetail productDetail = optionalProductDetail.get();
            response.put("price", productDetail.getPrice());
            response.put("sizes", sizeService.getAvailableSizesForProduct(productDetail.getProduct().getId())); // Lấy danh sách size
            response.put("colors", colorService.getAvailableColorsForProduct(productDetail.getProduct().getId())); // Lấy danh sách color
        } else {
            response.put("error", "Không tìm thấy sản phẩm!");
        }

        return response;
    }

    @PostMapping("/addProduct")
    public String addProductToOrder(@RequestParam("orderId") Integer orderId, @RequestParam("productDetailId") Integer productDetailId, @RequestParam("sizeId") Integer sizeId, @RequestParam("colorId") Integer colorId, @RequestParam("quantity") Integer quantity, RedirectAttributes redirectAttributes) {
        try {
            Optional<Order> optionalOrder = orderService.getOrderById(orderId);
            Optional<ProductDetail> optionalProductDetail = productDetailService.getProductDetailById(productDetailId);
            Optional<Size> optionalSize = sizeService.getSizeById(sizeId);
            Optional<Color> optionalColor = colorService.getColorById(colorId);

            if (optionalOrder.isEmpty() || optionalProductDetail.isEmpty() || optionalSize.isEmpty() || optionalColor.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ!");
                return "redirect:/admin/orders/detail?idOrder=" + orderId;
            }

            Order order = optionalOrder.get();
            ProductDetail productDetail = optionalProductDetail.get();
            Size size = optionalSize.get();
            Color color = optionalColor.get();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setPrice(productDetail.getPrice());
            orderDetail.setQuantity(quantity);

            orderDetailRepository.save(orderDetail);

            // Cập nhật tổng giá trị đơn hàng
            order.setTotalPrice(order.getTotalPrice().add(productDetail.getPrice().multiply(new BigDecimal(quantity))));
            orderService.saveOrder(order);

            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được thêm vào hóa đơn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/orders/detail?idOrder=" + orderId;
    }

    // Xử lý tăng/giảm số lượng sản phẩm trong đơn hàng
    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam("orderDetailId") Integer orderDetailId, @RequestParam("action") String action, Model model) {
        try {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(orderDetailId);
            if (optionalOrderDetail.isPresent()) {
                OrderDetail orderDetail = optionalOrderDetail.get();
                if (action.equals("increase")) {
                    orderDetail.setQuantity(orderDetail.getQuantity() + 1);
                } else if (action.equals("decrease") && orderDetail.getQuantity() > 1) {
                    orderDetail.setQuantity(orderDetail.getQuantity() - 1);
                }
                // Cập nhật tổng giá trị đơn hàng
                Order order = orderDetail.getOrder();
                BigDecimal priceDifference = orderDetail.getPrice();
                if (action.equals("increase")) {
                    order.setTotalPrice(order.getTotalPrice().add(priceDifference));
                } else if (action.equals("decrease")) {
                    order.setTotalPrice(order.getTotalPrice().subtract(priceDifference));
                }
                orderService.saveOrder(order);
                model.addAttribute("success", "Số lượng sản phẩm đã được cập nhật!");
            } else {
                model.addAttribute("error", "Không tìm thấy chi tiết đơn hàng.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật số lượng: " + e.getMessage());
        }
        return "redirect:/admin/orders/create";
    }

    // Xử lý xóa sản phẩm khỏi đơn hàng
    @PostMapping("/removeProduct")
    public String removeProduct(@RequestParam("orderDetailId") Integer orderDetailId, RedirectAttributes redirectAttributes) {
        try {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(orderDetailId);
            if (optionalOrderDetail.isPresent()) {
                OrderDetail orderDetail = optionalOrderDetail.get();
                Order order = orderDetail.getOrder();

                // Kiểm tra nếu order là null
                if (order != null) {
                    // Trừ giá trị sản phẩm khỏi tổng giá
                    BigDecimal priceTotal = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getQuantity()));
                    order.setTotalPrice(order.getTotalPrice().subtract(priceTotal));

                    // Xóa chi tiết đơn hàng khỏi đơn hàng
                    order.removeOrderDetail(orderDetail);
                    orderService.saveOrder(order);

                    // Thêm thông báo thành công
                    redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa khỏi đơn hàng!");
                    return "redirect:/admin/orders/detail?idOrder=" + order.getId();  // Sử dụng order.getId() sau khi kiểm tra null
                } else {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
                    return "redirect:/admin/orders";  // Quay lại trang danh sách đơn hàng
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi tiết đơn hàng.");
                return "redirect:/admin/orders";  // Quay lại trang danh sách đơn hàng
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "redirect:/admin/orders";  // Quay lại trang danh sách đơn hàng khi có lỗi
        }
    }


    // Xử lý xác nhận đặt hàng
    @PostMapping("/confirm/{id}")
    public String confirmOrder(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Order> optionalOrder = orderService.getOrderById(id);
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                order.setStatus("Confirmed");
                orderService.saveOrder(order);
                redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xác nhận!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xác nhận đơn hàng: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    // Lấy danh sách khách hàng và hiển thị modal chọn khách hàng
    @GetMapping("/{orderId}")
    public String viewOrder(@PathVariable("orderId") Integer orderId, Model model) {
        Optional<Order> order = orderService.getOrderById(orderId);
        List<Authority> customers = accountService.getAllCustomers(); // Lấy danh sách khách hàng
        model.addAttribute("order", order);
        model.addAttribute("customers", customers); // Truyền danh sách khách hàng vào view
        return "admin/orders/order_form"; // Tên trang hiện tại mà bạn muốn hiển thị
    }

    // Xử lý chọn khách hàng từ modal
    @PostMapping("/selectCustomer/{orderId}")
    public String selectCustomer(@PathVariable("orderId") Integer orderId, // orderId từ URL path
                                 @RequestParam("customerId") Integer customerId, // customerId từ query string
                                 @RequestParam("idOrder") Integer idOrder // idOrder từ query string
    ) {
        orderService.addCustomerToOrder(orderId, customerId); // Thêm khách hàng vào đơn hàng
        return "redirect:/admin/orders/detail/" + orderId; // Quay lại trang đơn hàng sau khi chọn khách hàng
    }

    @GetMapping("/searchCustomer")
    @ResponseBody
    public List<Account> searchCustomer(@RequestParam String email, @RequestParam String fullname) {
        List<Account> accountList = accountRepository.findCustomers(email, fullname);
        System.out.println("aaaaaaa" + accountList.size());
        return accountList;
    }
}
