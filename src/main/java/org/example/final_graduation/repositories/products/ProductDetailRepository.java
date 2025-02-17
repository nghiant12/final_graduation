package org.example.final_graduation.repositories.products;

import org.example.final_graduation.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {
    @Query("""
                SELECT pd FROM ProductDetail pd WHERE pd.product.id = :idProduct
            """)
    List<ProductDetail> findByProductID(@Param("idProduct") Integer idProduct);
}
