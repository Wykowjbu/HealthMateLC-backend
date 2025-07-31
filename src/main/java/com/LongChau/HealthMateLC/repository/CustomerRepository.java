package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    static String findEmailByCustomerId(Integer customerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findEmailByCustomerId'");
    }
}
