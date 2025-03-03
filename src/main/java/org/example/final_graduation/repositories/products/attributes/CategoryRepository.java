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
            SELECT c, COUNT(p) FROM Category c
            LEFT JOIN ProductDetail p on c.id = p.category.id
            GROUP BY c""")
    List<Object[]> findAllCategoriesWithProductCount();
}
