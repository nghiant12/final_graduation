package org.example.final_graduation.repositories;

import org.example.final_graduation.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
//    @Query("""
//                SELECT p FROM Promotion p
//                WHERE p.isActive = true
//                    AND p.startDate <= CURRENT_TIMESTAMP
//                    AND p.endDate >= CURRENT_TIMESTAMP
//            """)
//    List<Promotion> findActivePromotions();

    @Query("""
                SELECT p FROM Promotion p
                WHERE p.isActive = true
            """)
    List<Promotion> findActivePromotions();

    List<Promotion> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Promotion> findByStartDateAfter(LocalDateTime startDate);

    List<Promotion> findByEndDateBefore(LocalDateTime endDate);

    @Modifying
    @Query("UPDATE Promotion p SET p.isActive = false WHERE (p.endDate < CURRENT_TIMESTAMP OR p.remainingQuantity = 0) AND p.isActive = true")
    void deactivateExpiredPromotions();

    Promotion findById(int id);

    @Query("""
                SELECT p FROM Promotion p
                WHERE p.id = :id
            """)
    Promotion findByID(Integer id);
}