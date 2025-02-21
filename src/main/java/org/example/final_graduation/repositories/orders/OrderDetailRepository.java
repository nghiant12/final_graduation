package org.example.final_graduation.repositories.orders;

import org.example.final_graduation.entities.Order;
import org.example.final_graduation.entities.OrderDetail;
import org.example.final_graduation.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query("""
    SELECT od FROM OrderDetail od WHERE od.order.id = :idOrder
""")
    List<OrderDetail> findByOrderID(Integer idOrder);

    @Query("SELECT od FROM OrderDetail od WHERE od.order = :order AND od.productDetail = :productDetail")
    Optional<OrderDetail> findByOrderAndProductDetail(@Param("order") Order order,
                                                      @Param("productDetail") ProductDetail productDetail);

}
