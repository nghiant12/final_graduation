package org.example.final_graduation.repositories.orders;

import org.example.final_graduation.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query("""
    SELECT od FROM OrderDetail od WHERE od.order.id = :idOrder
""")
    List<OrderDetail> findByOrderID(Integer idOrder);
}
