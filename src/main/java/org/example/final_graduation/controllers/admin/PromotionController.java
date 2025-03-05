package org.example.final_graduation.controllers.admin;

import org.example.final_graduation.entities.Promotion;
import org.example.final_graduation.services.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/admin/promotions")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @GetMapping("")
    public String index(Model model) {
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
            redirectAttributes.addFlashAttribute("error", "Ngày không hợp lệ, vui lòng nhập đúng định dạng!");
            return "redirect:/admin/promotions/filter";
        }

        List<Promotion> filteredPromotions = promotionService.filterPromotions(start, end);

        model.addAttribute("promotions", filteredPromotions);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/promotions/index";
    }

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
            RedirectAttributes redirectAttributes
    ) {
        try {
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
}
