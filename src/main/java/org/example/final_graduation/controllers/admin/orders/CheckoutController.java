package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.controllers.admin.orders.EmailService;
import org.example.final_graduation.repositories.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckoutController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PromotionRepository promotionRepository;

    @GetMapping("/order/checkout")
    public String showCheckoutPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {

            // TODO: lấy dữ liệu thật từ database dựa theo authentication.getName()
            String fullName = "Tên người dùng đã đăng nhập";
            String email = "email@user.com";
            String phone = "0123456789";
            String address = "Địa chỉ mặc định";

            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("address", address);
            model.addAttribute("promotions", promotionRepository.findAll());
        }

        return "layout/checkout"; // trả về template checkout.html
    }

    @PostMapping("/order/checkout")
    public String processCheckout(@RequestParam String fullname,
                                  @RequestParam String email,
                                  @RequestParam String sdt,
                                  @RequestParam String diachi,
                                  Model model) {

        String orderDetails = "Tên: " + fullname + "\nSĐT: " + sdt + "\nĐịa chỉ: " + diachi;

        // Gửi email xác nhận
        emailService.sendConfirmationEmail(email, fullname, orderDetails);

        model.addAttribute("message", "Đặt hàng thành công! Vui lòng kiểm tra email.");

        return "layout/checkout"; // hiển thị lại trang với thông báo
    }
}
