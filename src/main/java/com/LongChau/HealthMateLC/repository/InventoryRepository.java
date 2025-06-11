package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    @Query("SELECT i FROM Inventory i WHERE i.number <= :threshold")
    List<Inventory> findLowStockProducts(@Param("threshold") Integer threshold);
    
    @Query("SELECT i FROM Inventory i WHERE i.number = 0")
    List<Inventory> findOutOfStockProducts();
    
    @Query("SELECT i FROM Inventory i ORDER BY i.number ASC")
    List<Inventory> findAllOrderByStockLevel();
}
