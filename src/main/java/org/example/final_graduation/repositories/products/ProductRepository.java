package org.example.final_graduation.repositories.products;

import org.example.final_graduation.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByName(String name);

    @Query("""
    SELECT pr FROM Product pr WHERE pr.id = :idProduct
""")
    Product findByID(@Param("idProduct") Integer idProduct);

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.productDetails pd " +
           "WHERE p.status = true " +
           "AND EXISTS (SELECT pd2 FROM ProductDetail pd2 " +
           "            WHERE pd2.product = p " +
           "            AND pd2.quantity > 0 " +
           "            AND pd2.available = true " +
           "            AND pd2.createdDate = (SELECT MAX(pd3.createdDate) FROM ProductDetail pd3 WHERE pd3.product = p))")
    List<Product> findTopProducts(Pageable pageable);

    @Query("SELECT p FROM Product p " +
           "JOIN p.productDetails pd " +
           "WHERE p.status = true " +
           "AND pd.available = true " +
           "GROUP BY p " +
           "ORDER BY SUM(pd.quantity) DESC")
    List<Product> findTopProductsByTotalQuantity(Pageable pageable);
}
