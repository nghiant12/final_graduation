package org.example.final_graduation.repositories.products.attributes;

import org.example.final_graduation.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRespository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);

    @Query("""
    SELECT ca FROM Category ca WHERE ca.id = :idCategory
""")
    Category findByID(@Param("idCategory") Integer idCategory);
}
