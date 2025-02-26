package org.example.final_graduation.repositories;

import org.example.final_graduation.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // Kiểm tra sự tồn tại của username hoặc email
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Tìm tài khoản theo ID
    @Query("""
        SELECT e FROM Employee e WHERE e.id = :id
    """)
    Employee findByID(@Param("id") Integer id);

//    // Tìm kiếm tài khoản theo từ khóa (username, fullname, hoặc email)
//    @Query("""
//        SELECT acc FROM Account acc
//        WHERE LOWER(acc.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
//           OR LOWER(acc.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
//           OR LOWER(acc.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
//    """)
//    List<Account> searchByKeyword(@Param("keyword") String keyword);
//
//    // Lấy danh sách tài khoản có role "user"
//    @Query("""
//                SELECT acc FROM Account acc
//                JOIN acc.authorities auth
//                JOIN auth.role r
//                WHERE r.name = :roleName
//            """)
//    List<Account> findAccountsByRole(@Param("roleName") String roleName);

//    //
//    @Query("""
//                SELECT acc FROM Account acc WHERE acc.id = :id
//            """)
//    Account findByID(Integer id);
//
//    @Query("""
//                SELECT auth FROM Authority auth
//                join Account acc on auth.id = acc.id
//                join Role r on auth.id = r.id
//            """)
//    List<Authority> findAllCustomers();

    @Query("""
        SELECT e FROM Employee e
        WHERE (e.email LIKE CONCAT('%', :query, '%')
            OR e.fullname LIKE CONCAT('%', :query, '%')
            OR e.phoneNumber LIKE CONCAT('%', :query, '%'))
    """)
    List<Employee> findEmployee(@Param("query") String query);

}
