package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByProductName(String productName);
    boolean existsByProductName(String productName);

    @Query("SELECT p FROM Product p WHERE " +
           "(:type = 'name' AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(:type = 'type' AND LOWER(p.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(:type = 'description' AND LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(:type = 'all' AND (LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.productType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    List<Product> searchProducts(@Param("keyword") String keyword, @Param("type") String type);

    // Phương thức cho phân trang
    @Query(value = "SELECT * FROM Products ORDER BY ProductID OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY",
           nativeQuery = true)
    List<Product> findProductsPaginated(@Param("offset") int offset, @Param("size") int size);

    // Đếm tổng số sản phẩm
    @Query("SELECT COUNT(p) FROM Product p")
    long getTotalProductCount();

    // Tìm kiếm với phân trang
    @Query(value = "SELECT * FROM Products p WHERE " +
           "(:type = 'name' AND p.ProductName LIKE %:keyword%) OR " +
           "(:type = 'type' AND p.ProductType LIKE %:keyword%) OR " +
           "(:type = 'unit' AND p.Unit LIKE %:keyword%) OR " +
           "(:type = 'description' AND p.Description LIKE %:keyword%) OR " +
           "(:type = 'all' AND (p.ProductName LIKE %:keyword% OR p.ProductType LIKE %:keyword% OR p.Unit LIKE %:keyword% OR p.Description LIKE %:keyword%)) " +
           "ORDER BY p.ProductID OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY",
           nativeQuery = true)
    List<Product> searchProductsPaginated(@Param("keyword") String keyword,
                                        @Param("type") String type,
                                        @Param("offset") int offset,
                                        @Param("size") int size);

    // Đếm số kết quả tìm kiếm
    @Query(value = "SELECT COUNT(*) FROM Products p WHERE " +
           "(:type = 'name' AND p.ProductName LIKE %:keyword%) OR " +
           "(:type = 'type' AND p.ProductType LIKE %:keyword%) OR " +
           "(:type = 'unit' AND p.Unit LIKE %:keyword%) OR " +
           "(:type = 'description' AND p.Description LIKE %:keyword%) OR " +
           "(:type = 'all' AND (p.ProductName LIKE %:keyword% OR p.ProductType LIKE %:keyword% OR p.Unit LIKE %:keyword% OR p.Description LIKE %:keyword%))",
           nativeQuery = true)
    long getSearchProductCount(@Param("keyword") String keyword, @Param("type") String type);
}
