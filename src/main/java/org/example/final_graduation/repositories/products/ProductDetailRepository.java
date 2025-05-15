package org.example.final_graduation.repositories.products;

import org.example.final_graduation.entities.Category;
import org.example.final_graduation.entities.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {
    List<ProductDetail> findByProductId(Integer productId);

    @Query("""
                SELECT pd FROM ProductDetail pd WHERE pd.product.id = :idProduct
            """)
    List<ProductDetail> findByProductID(@Param("idProduct") Integer idProduct);

    @Query("""
                SELECT pd
                FROM ProductDetail pd
                JOIN pd.product p
                WHERE pd.quantity > 0 AND pd.available = true AND p.status = true
            """)
    List<ProductDetail> findALL();

    @Query("""
                SELECT pd
                FROM ProductDetail pd
                JOIN pd.product p
                WHERE pd.quantity > 0 AND pd.available = true AND p.status = true
                ORDER BY pd.createdDate DESC 
            """)
    List<ProductDetail> findTop4Products(Pageable pageable);

    @Query("""
                SELECT pd
                FROM ProductDetail pd
                JOIN pd.product p
                WHERE pd.quantity > 0 AND pd.available = true AND p.status = true
                ORDER BY pd.quantity asc 
            """)
    List<ProductDetail> findTop4DacSac(Pageable pageable);

    @Query("""
                SELECT pd 
                FROM ProductDetail pd
                JOIN pd.product p
                WHERE pd.quantity > 0 
                AND p.status = true
                AND pd.product.name = :name
                AND pd.id <> :idProductDetail
            """)
    List<ProductDetail> find4TuongTu(
            @Param("name") String name,
            @Param("idProductDetail") Integer idProductDetail,
            Pageable pageable
    );


    @Query("""
                SELECT pd
                FROM ProductDetail pd
                JOIN pd.product p WHERE pd.quantity > 0 AND p.status = true AND pd.available = true
            """)
    Page<ProductDetail> paging(Pageable pageable);

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

    Page<ProductDetail> findByCategoryId(String categoryId, Pageable pageable);

    @Query(value = "select pd from ProductDetail pd JOIN pd.product p WHERE pd.quantity > 0 AND p.status = true AND pd.available = true AND pd.product.name like ?1")
    Page<ProductDetail> findByName(String name, Pageable pageable);

    @Query("SELECT pd FROM ProductDetail pd JOIN pd.product p WHERE pd.quantity > 0 AND p.status = true AND pd.available = true AND pd.price BETWEEN :minPrice AND :maxPrice AND pd.quantity > 0")
    Page<ProductDetail> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);

    @Query("SELECT pd FROM ProductDetail pd JOIN pd.product p WHERE pd.quantity > 0 AND p.status = true AND pd.available = true AND pd.price BETWEEN :minPrice AND :maxPrice AND pd.category IN :categories AND pd.quantity > 0")
    Page<ProductDetail> findByPriceBetweenAndCategoryIn(double minPrice, double maxPrice, List<Category> categories, Pageable pageable);

    @Query("SELECT pd.category.id, pd.category.name, COUNT(pd) FROM ProductDetail pd JOIN pd.product p WHERE pd.quantity > 0 AND p.status = true AND pd.available = true GROUP BY pd.category.id, pd.category.name")
    List<Object[]> countProductsByCategory();

    @Query("""
            SELECT COALESCE(SUM(pd.quantity), 0)
            FROM ProductDetail pd
            WHERE pd.product.id = :productId
            """)
    long sumQuantityByProductId(@Param("productId") Integer productId);


    @Query("""
                SELECT COUNT(p)
                FROM ProductDetail p
                WHERE p.product.id = :productId
                  AND p.brand.id = :brandId 
                  AND p.color.id = :colorId 
                  AND p.size.id = :sizeId
            """)
    long countExistingProductDetail(@Param("productId") Integer productId,
                                    @Param("brandId") Integer brandId,
                                    @Param("colorId") Integer colorId,
                                    @Param("sizeId") Integer sizeId);

    @Query("""
                SELECT p.quantity
                FROM ProductDetail p
                WHERE p.id = :productId
            """)
    Integer getStockByProductId(Integer productId);
}
