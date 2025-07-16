package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
}
