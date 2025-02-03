package org.example.final_graduation.repositories.products.attributes;

import org.example.final_graduation.entities.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    // Kiểm tra sự tồn tại của username hoặc email
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Tìm tài khoản theo ID
    @Query("""
        SELECT acc FROM Account acc WHERE acc.id = :id
    """)
    Account findByID(@Param("id") Integer id);

    // Tìm kiếm tài khoản theo từ khóa (username, fullname, hoặc email)
    @Query("""
        SELECT acc FROM Account acc 
        WHERE LOWER(acc.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(acc.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(acc.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Account> searchByKeyword(@Param("keyword") String keyword);

    // Lấy danh sách tài khoản có role "user"
    @Query("""
        SELECT acc FROM Account acc 
        JOIN acc.authorities auth 
        JOIN auth.role r 
        WHERE r.name = :roleName
    """)
    List<Account> findAccountsByRole(@Param("roleName") String roleName);
}
