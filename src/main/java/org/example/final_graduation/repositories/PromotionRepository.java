package org.example.final_graduation.repositories;

import org.example.final_graduation.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= CURRENT_TIMESTAMP AND p.endDate >= CURRENT_TIMESTAMP")
    List<Promotion> findActivePromotions();
}
