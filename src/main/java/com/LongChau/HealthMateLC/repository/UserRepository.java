package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findUsersByRole(String role);

    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT DISTINCT u.role FROM User u")
    List<String> getDistinctRoles();
}
