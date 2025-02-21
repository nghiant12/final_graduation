package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.AccountRepository;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.example.final_graduation.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailService orderDetailService;

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

        model.addAttribute("productDetails", productDetailService.getAllProductDetails());

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

        model.addAttribute("productDetails", productDetailService.getAllProductDetails());

        // Gửi thêm thông tin nếu cần thiết
        Optional<Order> selectedOrder = orderRepository.findById(idOrder);
        selectedOrder.ifPresent(order -> model.addAttribute("order", order));

        return "admin/orders/order_form";
    }


    // Xử lý hủy đơn hàng
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Integer orderId, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(orderId);
            orderService.deleteOrderById(orderId);
            redirectAttributes.addFlashAttribute("success", "Hóa đơn đã được hủy thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy và xóa hóa đơn: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    // API Lấy danh sách sản phẩm
    @GetMapping("/products")
    @ResponseBody
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // API Lấy danh sách thương hiệu, màu sắc, kích thước theo sản phẩm
    @GetMapping("/productDetails")
    @ResponseBody
    public Map<String, Object> getProductDeatils(@RequestParam("productId") Integer productId) {
        Map<String, Object> response = new HashMap<>();
        response.put("brands", brandService.getAvailableBrandsForProduct(productId));
        response.put("colors", colorService.getAvailableColorsForProduct(productId));
        response.put("sizes", sizeService.getAvailableSizesForProduct(productId));
        return response;
    }

    // API Lấy giá sản phẩm chi tiết theo thuộc tính
    @GetMapping("/productPrice")
    @ResponseBody
    public Map<String, Object> getProductPrice(@RequestParam("productId") Integer productId, @RequestParam("brandId") Integer brandId, @RequestParam("colorId") Integer colorId, @RequestParam("sizeId") Integer sizeId) {
        Optional<ProductDetail> productDetail = productDetailService.getProductDetail(productId, brandId, colorId, sizeId);
        if (productDetail.isPresent()) {
            return Map.of("price", productDetail.get().getPrice(), "productDetailId", productDetail.get().getId());
        } else {
            return Map.of("error", "Không tìm thấy sản phẩm phù hợp");
        }
    }

    // API Thêm sản phẩm vào đơn hàng
    @PostMapping("/addProductToOrder")
    public String addProductToOrder(
            @RequestParam(required = false) Integer orderID,
            @RequestParam Integer productDetailId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes
    ) {
        // Kiểm tra nếu orderID không tồn tại
        if (orderID == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn đơn hàng!");
            return "redirect:/admin/orders";
        }

        try {
            // Thêm sản phẩm vào đơn hàng
            orderDetailService.addProductToOrder(orderID, productDetailId, 1);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào hóa đơn!");
        } catch (Exception e) {
            // Xử lý lỗi nếu có vấn đề xảy ra khi thêm sản phẩm
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }

        // Quay về trang chi tiết đơn hàng
        return "redirect:/admin/orders/detail?idOrder=" + orderID;
    }

    // Xử lý tăng/giảm số lượng sản phẩm trong đơn hàng
    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam("orderDetailId") Integer orderDetailId,
                                 @RequestParam("action") String action,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(orderDetailId);
            if (optionalOrderDetail.isPresent()) {
                OrderDetail orderDetail = optionalOrderDetail.get();
                Order order = orderDetail.getOrder();
                Integer orderId = order.getId(); // Lấy ID của đơn hàng

                // Kiểm tra xem orderId có hợp lệ không
                if (orderId == null) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
                    return "redirect:/admin/orders";
                }

                // Cập nhật số lượng sản phẩm
                if ("increase".equals(action)) {
                    orderDetail.setQuantity(orderDetail.getQuantity() + 1);
                } else if ("decrease".equals(action) && orderDetail.getQuantity() > 1) {
                    orderDetail.setQuantity(orderDetail.getQuantity() - 1);
                }

                // Cập nhật tổng giá trị đơn hàng
                BigDecimal priceDifference = orderDetail.getPrice();
                if ("increase".equals(action)) {
                    order.setTotalPrice(order.getTotalPrice().add(priceDifference));
                } else if ("decrease".equals(action)) {
                    order.setTotalPrice(order.getTotalPrice().subtract(priceDifference));
                }

                // Lưu thay đổi vào DB
                orderService.saveOrder(order);
                redirectAttributes.addFlashAttribute("success", "Số lượng sản phẩm đã được cập nhật!");
                // Ghi log để debug nếu cần
                System.out.println("Redirecting to: /admin/orders/detail?idOrder=" + orderId);

                // Redirect về trang chi tiết đơn hàng đang chỉnh sửa
                return "redirect:/admin/orders/detail?idOrder=" + orderId;
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi tiết đơn hàng.");
                return "redirect:/admin/orders";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật số lượng: " + e.getMessage());
            return "redirect:/admin/orders";
        }
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

    @ResponseBody
    @GetMapping("/searchCustomer")
    public List<Account> searchCustomer(@RequestParam String query) {
        System.out.println("Searching for customers with query: " + query);
        List<Account> customers = accountRepository.findCustomers(query);
        System.out.println("Found customers: " + customers.size());
        return customers;
    }

}
