package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.CustomerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerMessageRepository extends JpaRepository<CustomerMessage, Integer> {
}
