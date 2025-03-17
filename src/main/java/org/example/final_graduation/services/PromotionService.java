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
}
