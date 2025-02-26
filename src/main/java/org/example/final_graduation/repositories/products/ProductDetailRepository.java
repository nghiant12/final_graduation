package org.example.final_graduation.repositories.products;

import org.example.final_graduation.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {
    @Query("""
                SELECT pd FROM ProductDetail pd WHERE pd.product.id = :idProduct
            """)
    List<ProductDetail> findByProductID(@Param("idProduct") Integer idProduct);

    @Query("""
    SELECT pd 
    FROM ProductDetail pd
    JOIN pd.product p
    WHERE pd.quantity > 0 AND p.status = true
""")
    List<ProductDetail> findALL();


    @Query("""
                SELECT pd FROM ProductDetail pd WHERE pd.id = :id
            """)
    ProductDetail findByID(@Param("id") Integer id);

    @Query("""
                SELECT pd FROM ProductDetail pd
                WHERE pd.product.id = :productId
                AND pd.brand.id = :brandId
                AND pd.color.id = :colorId
                AND pd.size.id = :sizeId
            """)
    Optional<ProductDetail> findByProductAndAttributes(
            @Param("productId") Integer productId,
            @Param("brandId") Integer brandId,
            @Param("colorId") Integer colorId,
            @Param("sizeId") Integer sizeId);
}
