package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.PromotionRepository;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private EmpolyeeService empolyeeService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("")
    public String showCreateOrderForm(Model model) {
        Order order = new Order();

        // Lấy thông tin nhân viên đang đăng nhập
        // String username = principal.getName(); // Lấy username từ session đăng nhập
        Optional<Employee> currentUser = empolyeeService.findByUsername("staff1"); // Tìm Employee theo username

        // Gán nhân viên hiện tại vào đơn hàng nếu tìm thấy
        currentUser.ifPresent(order::setEmployee);

        model.addAttribute("order", order);
        model.addAttribute("productDetails", productDetailRepository.findAll());

        List<Order> orders = orderRepository.findAllProcessingAtTheCounter();
        model.addAttribute("orders", orders);

        // Truyền tên người dùng vào model để hiển thị trên giao diện
        model.addAttribute("username", "username");

        return "admin/orders/order_form";
    }


    // Xử lý tạo đơn hàng mới
    @PostMapping("/create")
    public String createOrder(RedirectAttributes redirectAttributes) {
        try {
            Integer soLuongHoaDonCho = orderRepository.countByTypeStatus();

            if (soLuongHoaDonCho >= 5) {
                redirectAttributes.addFlashAttribute("error", "Vượt quá số lượng hóa đơn chờ! Không thể tạo thêm.");
                return "redirect:/admin/orders";
            }

            orderService.createNewOrder(); // Lưu hóa đơn
            redirectAttributes.addFlashAttribute("success", "Hóa đơn được tạo thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Không thể tạo hóa đơn!");
        }
        return "redirect:/admin/orders"; // Điều hướng lại trang danh sách hóa đơn
    }

    @GetMapping("/detail")
    public String showOrderDetail(@RequestParam Integer idOrder, Model model, Principal principal) {
//        String username = principal.getName();
        model.addAttribute("username", "username");
        // Lấy danh sách hóa đơn
        model.addAttribute("idOrder", idOrder);

        List<Order> orders = orderRepository.findAllProcessingAtTheCounter();
        model.addAttribute("orders", orders);

        List<ProductDetail> productDetails = productDetailRepository.findALL();
        System.out.println("Số sản phẩm: " + productDetails.size()); // Debug dữ liệu

        // Lấy chi tiết hóa đơn dựa trên idOrder

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(idOrder);
        model.addAttribute("orderDetails", orderDetails);

        model.addAttribute("productDetails", productDetails);

        model.addAttribute("totalPrice", orderDetailRepository.tongTien(idOrder));

        // Gửi thêm thông tin nếu cần thiết
        Optional<Order> selectedOrder = orderRepository.findById(idOrder);
        selectedOrder.ifPresent(order -> model.addAttribute("order", order));

        // Lấy danh sách khuyến mãi đang diễn ra
        List<Promotion> activePromotions = promotionRepository.findActivePromotions();
        model.addAttribute("promotions", activePromotions);

        return "admin/orders/order_form";
    }

    // Xử lý hủy đơn hàng
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Integer orderId, RedirectAttributes redirectAttributes) {
        try {
            // Lấy danh sách OrderDetail của đơn hàng
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(orderId);

            // Hoàn trả số lượng sản phẩm vào kho
            for (OrderDetail orderDetail : orderDetails) {
                ProductDetail productDetail = orderDetail.getProductDetail();
                int quantityToReturn = orderDetail.getQuantity();

                if (productDetail != null) {
                    productDetail.setQuantity(productDetail.getQuantity() + quantityToReturn);
                    productDetailService.saveProductDetail(productDetail);
                }
            }

            // Hủy đơn hàng
            orderService.cancelOrder(orderId);

            // Xóa đơn hàng khỏi DB
            orderService.deleteOrderById(orderId);

            redirectAttributes.addFlashAttribute("success", "Hóa đơn đã được hủy, sản phẩm đã được hoàn kho.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy hóa đơn: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    // API Thêm sản phẩm vào đơn hàng
    @PostMapping("/addProductToOrder")
    public String addProductToOrder(
            @RequestParam(required = false) Integer orderId,
            @RequestParam Integer productDetailId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes
    ) {
        // Kiểm tra nếu orderID không tồn tại
        if (orderId == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn đơn hàng!");
            return "redirect:/admin/orders";
        }
        try {
            ProductDetail productDetail = productDetailService.findById(productDetailId);
            if (productDetail.getQuantity() < quantity) {
                redirectAttributes.addFlashAttribute("error", "Số lượng sản phẩm không đủ!");
                return "redirect:/admin/orders/detail?idOrder=" + orderId;
            }
            orderDetailService.addProductToOrder(orderId, productDetailId, quantity);
            productDetail.setQuantity(productDetail.getQuantity() - quantity);
            productDetailService.saveProductDetail(productDetail);

            // Tính lại breakdown và cập nhật vào Order
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                java.util.List<OrderDetail> details = orderDetailRepository.findByOrderID(orderId);
                java.math.BigDecimal subTotal = details.stream().map(od -> od.getPrice()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                java.math.BigDecimal shippingFee = java.math.BigDecimal.ZERO;
                java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
                if (order.getPromotion() != null) {
                    discountAmount = subTotal.multiply(order.getPromotion().getDiscount()).divide(java.math.BigDecimal.valueOf(100));
                }
                java.math.BigDecimal totalPrice = subTotal.add(shippingFee).subtract(discountAmount);
                order.setSubTotal(subTotal);
                order.setShippingFee(shippingFee);
                order.setDiscountAmount(discountAmount);
                order.setTotalPrice(totalPrice.max(java.math.BigDecimal.ZERO));
                orderRepository.save(order);
            }

            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào hóa đơn!");
        } catch (Exception e) {
            // Xử lý lỗi nếu có vấn đề xảy ra khi thêm sản phẩm
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("totalPrice", orderDetailRepository.tongTien(orderId));
        // Quay về trang chi tiết đơn hàng
        return "redirect:/admin/orders/detail?idOrder=" + orderId;
    }

    @PostMapping("/updateQuantity/{orderDetailId}")
    public String updateQuantity(
            @PathVariable("orderDetailId") Integer orderDetailId,
            @RequestParam(value = "quantity", required = false) Integer newQuantity,
            @RequestParam(value = "idOrder", required = true) Integer orderId,
            @RequestParam(value = "action", required = false) String action,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(orderDetailId);
            if (!optionalOrderDetail.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi tiết đơn hàng.");
                return "redirect:/admin/orders/detail?idOrder=" + orderId;
            }

            OrderDetail orderDetail = optionalOrderDetail.get();
            ProductDetail productDetail = orderDetail.getProductDetail();
            Order order = orderDetail.getOrder();

            // Kiểm tra số lượng tồn kho hợp lệ
            int currentQuantity = orderDetail.getQuantity();
            int quantityInStock = productDetail.getQuantity();

            if (action != null) {
                if ("increase".equals(action)) {
                    if (quantityInStock > 0) {
                        newQuantity = currentQuantity + 1;
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Sản phẩm không đủ số lượng trong kho.");
                        return "redirect:/admin/orders/detail?idOrder=" + orderId;
                    }
                } else if ("decrease".equals(action) && currentQuantity > 1) {
                    newQuantity = currentQuantity - 1;
                } else {
                    redirectAttributes.addFlashAttribute("error", "Yêu cầu không hợp lệ.");
                    return "redirect:/admin/orders/detail?idOrder=" + orderId;
                }
            }

            // Đảm bảo số lượng tối thiểu là 1
            if (newQuantity == null || newQuantity < 1) {
                newQuantity = 1;
            }

            // Kiểm tra số lượng tồn kho
            if (productDetail.getQuantity() + currentQuantity < newQuantity) {
                redirectAttributes.addFlashAttribute("error", "Số lượng tồn kho không đủ.");
                return "redirect:/admin/orders/detail?idOrder=" + orderId;
            }

            // Cập nhật lại số lượng trong OrderDetail và tồn kho sản phẩm
            int stockRemaining = productDetail.getQuantity() + currentQuantity - newQuantity;
            orderDetail.setQuantity(newQuantity);
            productDetail.setQuantity(stockRemaining);

            // Cập nhật tổng tiền của từng sản phẩm
            BigDecimal newPrice = productDetail.getPrice().multiply(BigDecimal.valueOf(newQuantity));
            orderDetail.setPrice(newPrice);

            // Cập nhật tổng tiền của đơn hàng
            BigDecimal totalPrice = orderDetailRepository.tongTien(order.getId());
            order.setTotalPrice(totalPrice);

            // Lưu vào DB
            productDetailService.saveProductDetail(productDetail);
            orderDetailRepository.save(orderDetail);

            // Tính lại breakdown và cập nhật vào Order
            List<OrderDetail> details = orderDetailRepository.findByOrderID(order.getId());
            BigDecimal subTotal = details.stream().map(OrderDetail::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal shippingFee = BigDecimal.ZERO;
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (order.getPromotion() != null) {
                discountAmount = subTotal.multiply(order.getPromotion().getDiscount()).divide(BigDecimal.valueOf(100));
            }
            BigDecimal totalPriceIf = subTotal.add(shippingFee).subtract(discountAmount);
            order.setSubTotal(subTotal);
            order.setShippingFee(shippingFee);
            order.setDiscountAmount(discountAmount);
            order.setTotalPrice(totalPriceIf.max(BigDecimal.ZERO));
            orderRepository.save(order);

            // Lưu thông báo thành công vào session
            redirectAttributes.addFlashAttribute("success", "Số lượng đã được cập nhật!");
            redirectAttributes.addFlashAttribute("totalPrice", orderDetailRepository.tongTien(orderId));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật số lượng: " + e.getMessage());
        }

        return "redirect:/admin/orders/detail?idOrder=" + orderId;
    }

    // Xử lý xóa sản phẩm khỏi đơn hàng
    @PostMapping("/removeProduct")
    public String removeProduct(@RequestParam("orderDetailId") Integer orderDetailId, RedirectAttributes redirectAttributes) {
        try {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(orderDetailId);
            if (optionalOrderDetail.isPresent()) {
                OrderDetail orderDetail = optionalOrderDetail.get();
                Order order = orderDetail.getOrder();
                ProductDetail productDetail = orderDetail.getProductDetail(); // Lấy sản phẩm từ order detail

                if (order != null && productDetail != null) {
                    // Cộng lại số lượng vào tồn kho
                    int quantityToReturn = orderDetail.getQuantity();
                    productDetail.setQuantity(productDetail.getQuantity() + quantityToReturn);

                    // Cập nhật tổng giá trị đơn hàng
                    BigDecimal priceTotal = orderDetail.getPrice().multiply(new BigDecimal(quantityToReturn));
                    order.setTotalPrice(order.getTotalPrice().subtract(priceTotal));

                    // Xóa chi tiết đơn hàng khỏi đơn hàng
                    order.removeOrderDetail(orderDetail);
                    orderService.saveOrder(order);
                    productDetailService.saveProductDetail(productDetail); // Lưu thay đổi vào database
                    orderDetailRepository.delete(orderDetail); // Xóa OrderDetail khỏi DB

                    // Tính lại breakdown và cập nhật vào Order
                    java.util.List<OrderDetail> details = orderDetailRepository.findByOrderID(order.getId());
                    java.math.BigDecimal subTotal = details.stream().map(od -> od.getPrice()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                    java.math.BigDecimal shippingFee = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
                    if (order.getPromotion() != null) {
                        discountAmount = subTotal.multiply(order.getPromotion().getDiscount()).divide(java.math.BigDecimal.valueOf(100));
                    }
                    java.math.BigDecimal totalPrice = subTotal.add(shippingFee).subtract(discountAmount);
                    order.setSubTotal(subTotal);
                    order.setShippingFee(shippingFee);
                    order.setDiscountAmount(discountAmount);
                    order.setTotalPrice(totalPrice.max(java.math.BigDecimal.ZERO));
                    orderRepository.save(order);

                    // Thêm thông báo thành công
                    redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa khỏi đơn hàng!");
                    redirectAttributes.addFlashAttribute("totalPrice", orderDetailRepository.tongTien(order.getId()));

                    return "redirect:/admin/orders/detail?idOrder=" + order.getId();
                } else {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng hoặc sản phẩm.");
                    return "redirect:/admin/orders";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi tiết đơn hàng.");
                return "redirect:/admin/orders";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    // Xử lý xác nhận đặt hàng
    @Transactional
    @PostMapping("/confirm/{id}")
    public String confirmOrder(@PathVariable("id") Integer id,
                               @RequestParam("paymentMethod") String paymentMethod,
                               @RequestParam(value = "promotionId", required = false) Integer promotionId,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<Order> optionalOrder = orderService.getOrderById(id);
            if (optionalOrder.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
                return "redirect:/admin/orders";
            }

            Order order = optionalOrder.get();

            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(order.getId());
            if (orderDetails.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Đơn hàng chưa có sản phẩm, vui lòng thêm sản phẩm trước khi thanh toán.");
                return "redirect:/admin/orders/detail?idOrder=" + order.getId();
            }

            if (order.getCustomer() == null) {
                Optional<Customer> defaultAccount = customerRepository.findById(1);
                defaultAccount.ifPresent(order::setCustomer);
            }

            if (paymentMethod.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Chưa chọn phương thức thanh toán.");
                return "redirect:/admin/orders/detail?idOrder=" + order.getId();
            }

            order.setPaymentMethod(paymentMethod);

            BigDecimal totalPrice = orderDetailRepository.tongTien(order.getId());
            if (totalPrice == null) {
                totalPrice = BigDecimal.ZERO;
            }

            BigDecimal discountAmount = BigDecimal.ZERO;

            // Áp dụng khuyến mãi nếu hợp lệ
            if (promotionId != null) {
                Optional<Promotion> optionalPromotion = promotionRepository.findById(promotionId);
                if (optionalPromotion.isPresent()) {
                    Promotion promotion = optionalPromotion.get();
                    if (promotion.getActive() &&
                            promotion.getRemainingQuantity() > 0 &&
                            LocalDateTime.now().isAfter(promotion.getStartDate()) &&
                            LocalDateTime.now().isBefore(promotion.getEndDate()) &&
                            totalPrice.compareTo(promotion.getMinOrderValue()) >= 0) {

                        // Tính số tiền giảm giá
                        discountAmount = totalPrice.multiply(promotion.getDiscount()).divide(BigDecimal.valueOf(100));
                        totalPrice = totalPrice.subtract(discountAmount);

                        // Giảm số lượng mã giảm giá
                        promotion.setRemainingQuantity(promotion.getRemainingQuantity() - 1);
                        promotionRepository.save(promotion);

                        order.setPromotion(promotion);
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết số lượng.");
                        return "redirect:/admin/orders/detail?idOrder=" + order.getId();
                    }
                }
            }
            order.setDiscountAmount(discountAmount);
            order.setTotalPrice(totalPrice);
            order.setStatus("Completed");
            orderService.saveOrder(order);

            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã hoàn thành!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thanh toán đơn hàng: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }


    // Xử lý chọn khách hàng từ modal
    @PostMapping("/selectCustomer/{orderId}")
    public String selectCustomer(@PathVariable("orderId") Integer orderId,
                                 @RequestParam("customerId") Integer customerId,
                                 @RequestParam("idOrder") Integer idOrder,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (orderId == null || customerId == null) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ!");
                return "redirect:/admin/orders/detail?idOrder=" + orderId;
            }

            // Thêm khách hàng vào đơn hàng
            orderService.addCustomerToOrder(orderId, customerId);
            redirectAttributes.addFlashAttribute("success", "Đã thêm khách hàng vào hóa đơn!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm khách hàng vào hóa đơn!");
            e.printStackTrace(); // Log lỗi để debug
        }

        return "redirect:/admin/orders/detail?idOrder=" + orderId;
    }


    @ResponseBody
    @GetMapping("/searchCustomer")
    public List<Customer> searchCustomer(@RequestParam String query) {
        System.out.println("Searching for customers with query: " + query);
        List<Customer> customers = customerRepository.findCustomers(query);
        System.out.println("Found customers: " + customers.size());
        return customers;
    }

    @PostMapping("/addMultipleProductsToOrder")
    public String addMultipleProductsToOrder(
            @RequestParam Integer orderId,
            @RequestParam List<Integer> productDetailIds,
            @RequestParam List<Integer> quantities,
            RedirectAttributes redirectAttributes
    ) {
        if (productDetailIds.isEmpty() || quantities.isEmpty() || productDetailIds.size() != quantities.size()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ! Vui lòng chọn ít nhất một sản phẩm.");
            return "redirect:/admin/orders/detail?idOrder=" + orderId;
        }

        try {
            for (int i = 0; i < productDetailIds.size(); i++) {
                Integer productDetailId = productDetailIds.get(i);
                Integer quantity = quantities.get(i);

                // Kiểm tra quantity không null và không âm
                if (quantity == null || quantity <= 0) {
                    redirectAttributes.addFlashAttribute("error", "Số lượng sản phẩm không hợp lệ!");
                    return "redirect:/admin/orders/detail?idOrder=" + orderId;
                }

                ProductDetail productDetail = productDetailService.findById(productDetailId);
                if (productDetail.getQuantity() < quantity) {
                    redirectAttributes.addFlashAttribute("error", "Số lượng sản phẩm không đủ!");
                    return "redirect:/admin/orders/detail?idOrder=" + orderId;
                }

                // Thêm sản phẩm vào đơn hàng
                orderDetailService.addProductToOrder(orderId, productDetailId, quantity);
                productDetail.setQuantity(productDetail.getQuantity() - quantity);
                productDetailService.saveProductDetail(productDetail);
            }

            // Tính lại breakdown và cập nhật vào Order
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                java.util.List<OrderDetail> details = orderDetailRepository.findByOrderID(orderId);
                java.math.BigDecimal subTotal = details.stream().map(od -> od.getPrice()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                java.math.BigDecimal shippingFee = java.math.BigDecimal.ZERO;
                java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
                if (order.getPromotion() != null) {
                    discountAmount = subTotal.multiply(order.getPromotion().getDiscount()).divide(java.math.BigDecimal.valueOf(100));
                }
                java.math.BigDecimal totalPrice = subTotal.add(shippingFee).subtract(discountAmount);
                order.setSubTotal(subTotal);
                order.setShippingFee(shippingFee);
                order.setDiscountAmount(discountAmount);
                order.setTotalPrice(totalPrice.max(java.math.BigDecimal.ZERO));
                orderRepository.save(order);
            }

            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào hóa đơn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("totalPrice", orderDetailRepository.tongTien(orderId));
        return "redirect:/admin/orders/detail?idOrder=" + orderId;
    }

    // API trả về breakdown cho order (AJAX)
    @ResponseBody
    @GetMapping("/breakdown")
    public java.util.Map<String, Object> getOrderBreakdown(@RequestParam Integer idOrder, @RequestParam(required = false) Integer promotionId) {
        Order order = orderRepository.findById(idOrder).orElseThrow();
        java.util.List<OrderDetail> details = orderDetailRepository.findByOrderID(idOrder);
        java.math.BigDecimal subTotal = details.stream().map(od -> od.getPrice()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal shippingFee = java.math.BigDecimal.ZERO;
        java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
        if (promotionId != null) {
            Promotion promo = promotionRepository.findById(promotionId).orElse(null);
            if (promo != null) {
                discountAmount = subTotal.multiply(promo.getDiscount()).divide(java.math.BigDecimal.valueOf(100));
            }
        } else if (order.getPromotion() != null) {
            discountAmount = subTotal.multiply(order.getPromotion().getDiscount()).divide(java.math.BigDecimal.valueOf(100));
        }
        java.math.BigDecimal totalPrice = subTotal.add(shippingFee).subtract(discountAmount);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("subTotal", subTotal);
        result.put("shippingFee", shippingFee);
        result.put("discountAmount", discountAmount);
        result.put("totalPrice", totalPrice.max(java.math.BigDecimal.ZERO));
        return result;
    }

    // Hàm cập nhật breakdown cho order
    public void updateOrderBreakdown(Order order) {
        java.util.List<OrderDetail> details = orderDetailRepository.findByOrderID(order.getId());
        java.math.BigDecimal subTotal = details.stream().map(od -> od.getPrice()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal shippingFee = java.math.BigDecimal.ZERO;
        java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
        if (order.getPromotion() != null) {
            discountAmount = subTotal.multiply(order.getPromotion().getDiscount()).divide(java.math.BigDecimal.valueOf(100));
        }
        java.math.BigDecimal totalPrice = subTotal.add(shippingFee).subtract(discountAmount);
        order.setSubTotal(subTotal);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTotalPrice(totalPrice.max(java.math.BigDecimal.ZERO));
        orderRepository.save(order);
    }
}