package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.LoyaltyPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Integer> {

    // Lấy tất cả điểm loyalty của một khách hàng
    List<LoyaltyPoint> findByCustomerCustomerId(Integer customerId);

    // Tính tổng điểm của một khách hàng
    @Query("SELECT COALESCE(SUM(lp.points), 0) FROM LoyaltyPoint lp WHERE lp.customer.customerId = :customerId")
    Integer getTotalPointsByCustomerId(@Param("customerId") Integer customerId);
}
