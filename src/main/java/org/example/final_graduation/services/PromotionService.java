package org.example.final_graduation.services;

import org.example.final_graduation.entities.Promotion;
import org.example.final_graduation.repositories.PromotionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> getPromotionById(Integer id) {
        return promotionRepository.findById(id);
    }

    public Promotion savePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public void deletePromotion(Integer id) {
        promotionRepository.deleteById(id);
    }

    public List<Promotion> filterPromotions(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return promotionRepository.findByStartDateBetween(startDate, endDate);
        } else if (startDate != null) {
            return promotionRepository.findByStartDateAfter(startDate);
        } else if (endDate != null) {
            return promotionRepository.findByEndDateBefore(endDate);
        } else {
            return promotionRepository.findAll();
        }
    }

    @Scheduled(cron = "0 * * * * ?") // Chạy mỗi phút
    @Transactional
    public void checkAndDeactivateExpiredPromotions() {
        promotionRepository.deactivateExpiredPromotions();
        System.out.println("Kiểm tra và cập nhật trạng thái khuyến mãi: " + LocalDateTime.now());
    }

    public Optional<Promotion> validatePromotionForUser(String code, java.math.BigDecimal orderTotal) {
        Promotion promotion = promotionRepository.findByCode(code);
        if (promotion == null) return Optional.empty();
        LocalDateTime now = LocalDateTime.now();
        if (!Boolean.TRUE.equals(promotion.getActive())) return Optional.empty();
        if (promotion.getRemainingQuantity() == null || promotion.getRemainingQuantity() <= 0) return Optional.empty();
        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) return Optional.empty();
        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) return Optional.empty();
        if (promotion.getMinOrderValue() != null && orderTotal.compareTo(promotion.getMinOrderValue()) < 0) return Optional.empty();
        return Optional.of(promotion);
    }
}
