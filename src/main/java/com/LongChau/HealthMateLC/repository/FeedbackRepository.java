package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Feedback;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByPharmacy_PharmacyId(int pharmacyId);

    List<Feedback> findByCustomer_CustomerId(int customerId);

    // Đếm số đánh giá dưới hoặc bằng 3 sao
    long countByRatingLessThanEqual(Integer rating);
}
