package org.example.final_graduation.repositories.orders;

import org.example.final_graduation.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("""
            select o from Order o where o.status like "Processing"
            """)
    List<Order> findAllProcessing();

    @Query("""
    SELECT COUNT(*)
    FROM Order od
    WHERE od.type = 'At the counter'
    AND od.status = 'Processing'
""")
    Integer countByTypeStatus();
}
