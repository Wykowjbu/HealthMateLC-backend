package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByProductName(String productName);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:type = 'name' AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(:type = 'type' AND LOWER(p.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(:type = 'description' AND LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(:type = 'all' AND (LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.productType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    List<Product> searchProducts(@Param("keyword") String keyword, @Param("type") String type);
}
