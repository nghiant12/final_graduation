package org.example.final_graduation.repositories.products.attributes;

import org.example.final_graduation.entities.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {
    boolean existsByName(String name);

    @Query("""
    SELECT sz FROM Size sz WHERE sz.id = :idSize
""")
    Size findByID(@Param("idSize") Integer idSize);
}
