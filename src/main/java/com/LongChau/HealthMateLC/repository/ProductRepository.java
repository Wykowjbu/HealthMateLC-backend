package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Basic existence checks
    Optional<Product> findByProductName(String productName);
    boolean existsByProductName(String productName);

    // Search by individual fields with pagination
    Page<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);
    Page<Product> findByProductTypeContainingIgnoreCase(String productType, Pageable pageable);
    Page<Product> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    // Search across 3 main fields with pagination
    Page<Product> findByProductNameContainingIgnoreCaseOrProductTypeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String productName, String productType, String description, Pageable pageable);

    // Search without pagination (for backward compatibility)
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByProductTypeContainingIgnoreCase(String productType);
    List<Product> findByDescriptionContainingIgnoreCase(String description);
    List<Product> findByProductNameContainingIgnoreCaseOrProductTypeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String productName, String productType, String description);

    // Get products by price range
    Page<Product> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    // Get products with price greater than
    Page<Product> findByPriceGreaterThan(double price, Pageable pageable);
    List<Product> findByPriceGreaterThan(double price);

    // Get products with price less than
    Page<Product> findByPriceLessThan(double price, Pageable pageable);
    List<Product> findByPriceLessThan(double price);
}
