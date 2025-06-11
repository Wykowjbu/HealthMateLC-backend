package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByPhone(String phone);
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByFullNameContainingIgnoreCase(String fullName);
    
    @Query("SELECT c FROM Customer c WHERE c.totalPoints >= :minPoints")
    List<Customer> findCustomersWithMinPoints(@Param("minPoints") Integer minPoints);
    
    @Query("SELECT c FROM Customer c ORDER BY c.totalPoints DESC")
    List<Customer> findTopCustomersByPoints();
}
