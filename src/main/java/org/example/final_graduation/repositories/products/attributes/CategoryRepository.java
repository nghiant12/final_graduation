package org.example.final_graduation.repositories.products.attributes;

import org.example.final_graduation.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);

    @Query("""
    SELECT ca FROM Category ca WHERE ca.id = :idCategory
""")
    Category findByID(@Param("idCategory") Integer idCategory);

    @Query("""
            SELECT c, COUNT(pd) FROM Category c
            LEFT JOIN ProductDetail pd on c.id = pd.category.id
            JOIN pd.product p WHERE pd.quantity > 0 AND p.status = true AND pd.available = true
            GROUP BY c""")
    List<Object[]> findAllCategoriesWithProductCount();
}
