package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByCustomer_CustomerId(Integer customerId);
    
    List<Feedback> findByPharmacy_PharmacyId(Integer pharmacyId);
    
    List<Feedback> findByRating(Integer rating);
    
    List<Feedback> findByHandledByUserIsNull();
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.pharmacy.pharmacyId = :pharmacyId")
    Double getAverageRatingByPharmacy(@Param("pharmacyId") Integer pharmacyId);
    
    @Query("SELECT f FROM Feedback f WHERE f.rating <= :maxRating ORDER BY f.feedbackDate DESC")
    List<Feedback> findLowRatingFeedbacks(@Param("maxRating") Integer maxRating);
}
