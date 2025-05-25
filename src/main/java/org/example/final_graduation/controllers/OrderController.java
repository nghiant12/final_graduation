package org.example.final_graduation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper; // ObjectMapper có thể không cần nữa nếu chỉ dùng @RequestBody
import org.example.final_graduation.dto.OrderDetailRequest;
import org.example.final_graduation.dto.OrderRequest;
import org.example.final_graduation.dto.OrderResponse;
import org.example.final_graduation.entities.Customer;
import org.example.final_graduation.entities.ProductDetail; // Cần để tính lại giá
import org.example.final_graduation.repositories.PromotionRepository;
import org.example.final_graduation.repositories.products.ProductDetailRepository; // Cần để lấy ProductDetail
import org.example.final_graduation.services.CustomerService;
import org.example.final_graduation.services.OrderService;
import org.example.final_graduation.services.VNPayService; // Import VNPayService
import org.example.final_graduation.services.PromotionService; // Import PromotionService
import org.example.final_graduation.services.GHNService; // Import GHNService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller; // Thay đổi lại thành @Controller nếu xử lý return là trang HTML
import org.springframework.web.bind.annotation.*; // Thay đổi từ @Controller sang @RestController hoặc dùng ResponseEntity
import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller("clientOrderController") // Đổi tên bean ở đây
@RequestMapping("/api/orders") // Thay đổi đường dẫn API
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductDetailRepository productDetailRepository; // Inject để lấy thông tin sản phẩm

    @Autowired
    private VNPayService vnPayService; // Inject VNPayService

    @Autowired
    private PromotionService promotionService; // Inject PromotionService

    @Autowired
    private GHNService ghnService; // Inject GHNService

    // ObjectMapper không cần thiết ở đây nữa vì @RequestBody sẽ xử lý JSON

    @PostMapping // Endpoint này vẫn trả về JSON nên có thể để @ResponseBody hoặc cả class là @RestController
    @ResponseBody // Đảm bảo phương thức này trả về JSON
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest, // Nhận toàn bộ request body
                                          Principal principal,
                                          HttpServletRequest httpServletRequest) { // Thêm HttpServletRequest

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser");
        
        if (!isAuthenticated || principal == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập để đặt hàng."));
        }

        Optional<Customer> currentCustomer = customerService.findByUsername(principal.getName());
        if (currentCustomer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Lỗi: Không tìm thấy thông tin khách hàng đã xác thực."));
        }
        orderRequest.setCustomerId(currentCustomer.get().getId());

        if (orderRequest.getEmployeeId() == null) {
            orderRequest.setEmployeeId(1); // Hoặc logic gán employeeId mặc định khác
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        if (orderRequest.getOrderDetails() == null || orderRequest.getOrderDetails().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Giỏ hàng không được để trống."));
        }

        for (var detail : orderRequest.getOrderDetails()) {
            if (detail.getProductDetailId() == null || detail.getQuantity() == null || detail.getQuantity() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Thông tin chi tiết sản phẩm không hợp lệ."));
            }
            ProductDetail pd = productDetailRepository.findById(detail.getProductDetailId()).orElse(null);
            if (pd == null || !pd.getAvailable()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Sản phẩm với ID " + detail.getProductDetailId() + " không tồn tại hoặc không có sẵn."));
            }
            if (pd.getQuantity() < detail.getQuantity()) {
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Sản phẩm " + pd.getProduct().getName() + " không đủ số lượng tồn kho."));
            }
            detail.setPrice(pd.getPrice());
            subTotal = subTotal.add(pd.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
        }
        orderRequest.setSubTotal(subTotal);

        // Tính phí ship (giả sử lấy từ orderRequest, nếu không có thì mặc định 30000)
        BigDecimal shippingFee = orderRequest.getShippingFee();
        if (shippingFee == null) {
            shippingFee = BigDecimal.valueOf(30000); // TODO: lấy thực tế từ GHNService nếu có địa chỉ/ward/district/weight
        }
        orderRequest.setShippingFee(shippingFee);

        // Áp dụng mã giảm giá nếu có
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (orderRequest.getPromotionCode() != null && !orderRequest.getPromotionCode().isEmpty()) {
            var promoOpt = promotionService.validatePromotionForUser(orderRequest.getPromotionCode(), subTotal);
            if (promoOpt.isPresent()) {
                var promo = promoOpt.get();
                discountAmount = subTotal.multiply(promo.getDiscount()).divide(BigDecimal.valueOf(100));
            }
        }
        orderRequest.setDiscountAmount(discountAmount);

        // Tổng tiền cuối cùng
        BigDecimal finalTotal = subTotal.add(shippingFee).subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) finalTotal = BigDecimal.ZERO;
        orderRequest.setTotalPrice(finalTotal);
        orderRequest.setType("Online");
        // Lấy IP address từ request và truyền vào orderRequest nếu VNPayService của bạn cần nó từ đây
        // String vnp_IpAddr = VNPayConfig.getIpAddress(httpServletRequest);
        // Tuy nhiên, VNPayService của bạn đang hardcode IP, nên chưa cần thiết ở đây.

        try {
            Map<String, Object> responseBody = new HashMap<>();

            String paymentMethod = orderRequest.getPaymentMethod();
            if (paymentMethod == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Phương thức thanh toán không được để trống"));
            }

            if ("VNPay".equalsIgnoreCase(paymentMethod)) {
                // For VNPay, create payment URL first without creating order
                String vnpayUrl = vnPayService.createOrder(
                    orderRequest.getTotalPrice().intValue(),
                    "Thanh toan don hang moi",
                    httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" +
                    httpServletRequest.getServerPort() + "/api/orders/vnpay-return"
                );
                
                // Store order request in session for later use
                httpServletRequest.getSession().setAttribute("pendingOrder", orderRequest);
                
                responseBody.put("paymentUrl", vnpayUrl);
                responseBody.put("paymentMethod", "VNPay");
                responseBody.put("redirect", true);
                responseBody.put("subTotal", subTotal);
                responseBody.put("shippingFee", shippingFee);
                responseBody.put("discountAmount", discountAmount);
                responseBody.put("finalTotal", finalTotal);
            } else if ("Cash".equalsIgnoreCase(paymentMethod)) {
                // For Cash on delivery, create order immediately
                OrderResponse orderResponse = orderService.createOrder(orderRequest);
                responseBody.put("order", orderResponse);
                responseBody.put("paymentMethod", "Cash");
                responseBody.put("subTotal", subTotal);
                responseBody.put("shippingFee", shippingFee);
                responseBody.put("discountAmount", discountAmount);
                responseBody.put("finalTotal", finalTotal);
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Phương thức thanh toán không hợp lệ"));
            }
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Đã xảy ra lỗi khi xử lý đơn hàng: " + e.getMessage()));
        }
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            int paymentStatus = vnPayService.orderReturn(request);
            String responseCode = request.getParameter("vnp_ResponseCode");

            // Get pending order from session
            OrderRequest pendingOrder = (OrderRequest) request.getSession().getAttribute("pendingOrder");
            if (pendingOrder == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đơn hàng!");
                return "redirect:/checkout";
            }

            // Remove pending order from session
            request.getSession().removeAttribute("pendingOrder");

            if (paymentStatus == 1) { // Thành công
                try {
                    // Create the order now that payment is successful
                    OrderResponse orderResponse = orderService.createOrder(pendingOrder);
                    redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
                    return "redirect:/thank-you";
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Thanh toán thành công nhưng không thể tạo đơn hàng: " + e.getMessage());
                    return "redirect:/checkout";
                }
            } else if (paymentStatus == 0) { // Thất bại
                String errorMessage = getVNPayErrorMessage(responseCode);
                redirectAttributes.addFlashAttribute("error", "Thanh toán thất bại: " + errorMessage);
                return "redirect:/checkout";
            } else { // Chữ ký không hợp lệ
                redirectAttributes.addFlashAttribute("error", "Lỗi xác thực thanh toán. Giao dịch không hợp lệ.");
                return "redirect:/checkout";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý thanh toán: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    private String getVNPayErrorMessage(String responseCode) {
        return switch (responseCode) {
            case "24" -> "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "09" -> "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng";
            case "10" -> "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11" -> "Giao dịch không thành công do: Đã hết hạn chờ thanh toán";
            case "12" -> "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa";
            case "13" -> "Giao dịch không thành công do: Khách hàng nhập sai mật khẩu xác thực giao dịch (OTP)";
            case "51" -> "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch";
            case "65" -> "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày";
            case "75" -> "Ngân hàng thanh toán đang bảo trì";
            case "79" -> "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định";
            default -> "Giao dịch không thành công do lỗi khác (mã lỗi: " + responseCode + ")";
        };
    }
}