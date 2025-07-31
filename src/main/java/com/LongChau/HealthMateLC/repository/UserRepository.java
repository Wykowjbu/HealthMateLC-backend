package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findUsersByRole(String role);

    User findUserByUsername(String username);

    @Query("SELECT DISTINCT u.role FROM User u")
    List<String> getDistinctRoles();

    Optional<User> findByUsername(String username); // Thay User bằng Optional<User>

    List<User> findByRole(String role);

    List<User> findByIsActive(Boolean isActive);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> findActiveUserByUsername(@Param("username") String username);

    boolean existsByUsername(String username);

    // Thêm phương thức mới từ nhánh managercontroller
    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.role = :role")
    User findByUserIdAndRole(@Param("userId") Integer userId, @Param("role") String role);

    @Query("SELECT u FROM User u JOIN u.userInformation ui WHERE ui.pharmacy.pharmacyId = :pharmacyId AND u.role = 'employee'")
    List<User> findEmployeesByPharmacyId(@Param("pharmacyId") Integer pharmacyId);
}