package org.example.final_graduation.repositories.products.attributes;

import org.example.final_graduation.entities.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
    boolean existsByName(String name);

    @Query("""
                SELECT cl FROM Color cl WHERE cl.id = :idColor
            """)
    Color findByID(@Param("idColor") Integer idColor);

    @Query("""
                SELECT DISTINCT pd.color FROM ProductDetail pd WHERE pd.id = :idProduct
            """)
    List<Color> findColorsByProduct(@Param("productId") Integer productId);
}
