package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    
    List<Product> findByProductType(String productType);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("SELECT p FROM Product p WHERE p.price <= :maxPrice ORDER BY p.price ASC")
    List<Product> findProductsUnderPrice(@Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT DISTINCT p.productType FROM Product p")
    List<String> findAllProductTypes();
}
