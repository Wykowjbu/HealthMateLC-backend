package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByCustomer_CustomerId(Integer customerId);
    
    List<Notification> findByMessageType(String messageType);
    
    List<Notification> findByCustomer_CustomerIdOrderBySentDateDesc(Integer customerId);
}
