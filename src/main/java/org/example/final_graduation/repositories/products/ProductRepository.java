package org.example.final_graduation.repositories.products;

import org.example.final_graduation.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByName(String name);

    @Query("""
    SELECT pr FROM Product pr WHERE pr.id = :idProduct
""")
    Product findByID(@Param("idProduct") Integer idProduct);
}
