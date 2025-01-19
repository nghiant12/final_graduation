package org.example.final_graduation.repositories;

import org.example.final_graduation.entities.Account;
import org.example.final_graduation.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    //Nháp, khi nào dùng đăng nhập thì xóa
    @Query("""
                SELECT acc FROM Account acc WHERE acc.id = :id
            """)
    Account findByID(Integer id);

    @Query("""
                SELECT auth FROM Authority auth
                join Account acc on auth.id = acc.id
                join Role r on auth.id = r.id
            """)
    List<Authority> findAllCustomers();

    @Query("""
    SELECT acc FROM Account acc
    JOIN Authority aut ON aut.account.id = acc.id
    JOIN Role r ON aut.role.id = r.id
    WHERE acc.email LIKE CONCAT('%', :email, '%') AND r.id = 1
""")
    List<Account> findCustomersByEmail(@Param("email") String email);
}
