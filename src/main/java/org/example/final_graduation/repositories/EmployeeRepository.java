package org.example.final_graduation.repositories;

import org.example.final_graduation.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Employee> findByUsername(String username);

    Optional<Employee> findByEmail(String email);

    // Truy vấn danh sách nhân viên có role_id = 1 (staff)
    @Query("SELECT e FROM Employee e WHERE e.role.id = :roleId")
    List<Employee> findByRoleId(@Param("roleId") int roleId);

    // Tìm tài khoản theo ID
    @Query("""
                SELECT e FROM Employee e WHERE e.id = :id
            """)
    Employee findByID(@Param("id") Integer id);

    @Query("""
                SELECT e FROM Employee e
                WHERE (e.email LIKE CONCAT('%', :query, '%')
                    OR e.fullname LIKE CONCAT('%', :query, '%')
                    OR e.phoneNumber LIKE CONCAT('%', :query, '%'))
            """)
    List<Employee> findEmployee(@Param("query") String query);

}
