package org.example.final_graduation.controllers.admin;

import jakarta.validation.Valid;
import org.example.final_graduation.entities.Promotion;
import org.example.final_graduation.services.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/promotions")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @GetMapping("")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        model.addAttribute("promotions", promotionService.getAllPromotions());
        model.addAttribute("promotion", new Promotion()); // Để modal có đối tượng rỗng
        return "admin/promotions/index";
    }

    @GetMapping("/filter")
    public String filter(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model,
            RedirectAttributes redirectAttributes) {

        LocalDateTime start = null;
        LocalDateTime end = null;

        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Ngày không hợp lệ, vui lòng nhập đúng định dạng yyyy-MM-dd!");
            return "redirect:/admin/promotions";
        }

        List<Promotion> filteredPromotions = promotionService.filterPromotions(start, end);

        if (filteredPromotions == null || filteredPromotions.isEmpty()) {
            filteredPromotions = Collections.emptyList();
        }

        model.addAttribute("promotions", filteredPromotions);
        model.addAttribute("promotion", new Promotion()); // Đảm bảo có object này trong model
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/promotions/index";
    }

    @Transactional
    @PostMapping("add")
    public String add(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("discount") BigDecimal discount,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            @RequestParam("minOrderValue") BigDecimal minOrderValue,
            @RequestParam("remainingQuantity") Integer remainingQuantity,
            Model model,
            @Valid @ModelAttribute("promotion") Promotion promotion,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
                return "redirect:/admin/promotions";
            }


            LocalDateTime now = LocalDateTime.now();

            // Kiểm tra ngày bắt đầu phải lớn hơn hiện tại
            if (promotion.getStartDate().isBefore(now)) {
                redirectAttributes.addFlashAttribute("error", "Ngày bắt đầu phải lớn hơn thời điểm hiện tại.");
                return "redirect:/admin/promotions";
            }

            // Kiểm tra ngày kết thúc phải lớn hơn ngày bắt đầu
            if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
                redirectAttributes.addFlashAttribute("error", "Ngày kết thúc phải lớn hơn ngày bắt đầu.");
                return "redirect:/admin/promotions";
            }

            Promotion newPromotion = new Promotion();
            newPromotion.setName(name);
            newPromotion.setDescription(description);
            newPromotion.setDiscount(discount);
            newPromotion.setMinOrderValue(minOrderValue);
            newPromotion.setRemainingQuantity(remainingQuantity);
            newPromotion.setStartDate(startDate);
            newPromotion.setEndDate(endDate);

            promotionService.savePromotion(newPromotion);
            redirectAttributes.addFlashAttribute("success", "Thêm khuyến mãi thành công!");

            return "redirect:/admin/promotions";

        } catch (Exception e) {
            model.addAttribute("promotion", new Promotion()); // Thêm đối tượng rỗng để tránh lỗi
            model.addAttribute("error", "Có lỗi xảy ra khi thêm khuyến mãi!");
            return "admin/promotions/index";
        }
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("promotion") Promotion promotion, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
            return "redirect:/admin/promotions";
        }

        Optional<Promotion> existingPromotion = promotionService.getPromotionById(promotion.getId());
        if (existingPromotion.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khuyến mãi.");
            return "redirect:/admin/promotions";
        }

        Promotion updatedPromotion = existingPromotion.get();

        // Không cho sửa ngày bắt đầu
        promotion.setStartDate(updatedPromotion.getStartDate());

        // Kiểm tra ngày kết thúc phải lớn hơn ngày bắt đầu
        if (promotion.getEndDate().isBefore(updatedPromotion.getStartDate())) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc phải lớn hơn ngày bắt đầu.");
            return "redirect:/admin/promotions";
        }

        updatedPromotion.setName(promotion.getName());
        updatedPromotion.setDescription(promotion.getDescription());
        updatedPromotion.setDiscount(promotion.getDiscount());
        updatedPromotion.setMinOrderValue(promotion.getMinOrderValue());
        updatedPromotion.setRemainingQuantity(promotion.getRemainingQuantity());
        updatedPromotion.setEndDate(promotion.getEndDate());

        promotionService.savePromotion(updatedPromotion);
        redirectAttributes.addFlashAttribute("success", "Cập nhật khuyến mãi thành công!");
        return "redirect:/admin/promotions";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivatePromotion(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<Promotion> optionalPromotion = promotionService.getPromotionById(id);
        if (optionalPromotion.isPresent()) {
            Promotion promotion = optionalPromotion.get();
            promotion.setActive(false);
            promotionService.savePromotion(promotion);
            redirectAttributes.addFlashAttribute("success", "Mã khuyến mãi đã ngừng hoạt động.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy mã khuyến mãi.");
        }
        return "redirect:/admin/promotions";
    }

}
