package org.example.final_graduation.repositories.orders;

import org.example.final_graduation.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("""
            select o from Order o where o.status like "Processing"
            """)
    List<Order> findAllProcessing();

    @Query("""
            select o from Order o where o.id = :id
            """)
    Order findByID(@Param("id") Integer id);

    @Query("""
                SELECT o FROM Order o
                WHERE NOT (o.type = 'At the counter' AND o.status = 'Processing')
            """)
    List<Order> findAllOrderManager();

    @Query("""
                SELECT COUNT(*)
                FROM Order od
                WHERE od.type = 'At the counter'
                AND od.status = 'Processing'
            """)
    Integer countByTypeStatus();

    @Query("""
                SELECT o FROM Order o
                WHERE o.createdDate BETWEEN :start AND :end
                AND NOT (o.type = 'At the counter' AND o.status = 'Processing')
            """)
    List<Order> findByCreatedDateBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT o FROM Order o WHERE o.createdDate BETWEEN :start AND :end " +
            "AND (:type IS NULL OR o.type = :type) " +
            "AND NOT (o.type = 'At the counter' AND o.status = 'Processing')")
    List<Order> findByCreatedDateBetweenAndType(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("type") String type
    );


    @Query("""
                SELECT o FROM Order o 
                WHERE o.type = :type
                AND NOT (o.type = 'At the counter' AND o.status = 'Processing')
            """)
    List<Order> findByType(
            @Param("type") String type
    );

    @Query("""
    SELECT o FROM Order o 
    WHERE o.type = 'At the counter' 
    AND o.status = 'Processing'
""")
    List<Order> findAllProcessingAtTheCounter();
}
