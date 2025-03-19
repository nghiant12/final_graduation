package org.example.final_graduation.repositories;

import org.example.final_graduation.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // Tìm khách hàng theo username
    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByEmail(String email);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Tìm tài khoản theo ID
    @Query("""
                SELECT c FROM Customer c WHERE c.id = :id
            """)
    Customer findByID(@Param("id") Integer id);

    // Tìm kiếm tài khoản theo từ khóa (username, fullname, hoặc email)
    @Query("""
                SELECT c FROM Customer c
                WHERE LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(c.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Customer> searchByKeyword(@Param("keyword") String keyword);

    @Query("""
                SELECT c FROM Customer c
                WHERE (c.email LIKE CONCAT('%', :query, '%')
                    OR c.fullname LIKE CONCAT('%', :query, '%')
                    OR c.phoneNumber LIKE CONCAT('%', :query, '%'))
                AND c.id <> 1
            """)
    List<Customer> findCustomers(@Param("query") String query);

}
