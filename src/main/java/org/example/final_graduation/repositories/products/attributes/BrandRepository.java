package org.example.final_graduation.repositories.products.attributes;

import org.example.final_graduation.entities.Brand;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    boolean existsByName(String name);

    @Query("""
                SELECT br FROM Brand br WHERE br.id = :idBrand
            """)
    Brand findByID(@Param("idBrand") Integer idBrand);

    @Query("""
                SELECT DISTINCT pd.brand FROM ProductDetail pd WHERE pd.id = :idProduct
            """)
    List<Brand> findBrandsByProduct(@Param("productId") Integer productId);
}
