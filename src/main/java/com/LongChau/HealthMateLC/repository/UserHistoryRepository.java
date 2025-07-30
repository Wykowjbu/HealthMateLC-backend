package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
    
    // Method từ nhánh managercontroller - tìm theo danh sách user IDs
    List<UserHistory> findByUser_UserIdIn(List<Integer> userIds);
    
    // Method từ nhánh dev - tìm theo userId với JOIN FETCH và ORDER BY
    @Query("SELECT uh FROM UserHistory uh JOIN FETCH uh.pharmacy WHERE uh.user.userId = :userId ORDER BY uh.startTime DESC")
    List<UserHistory> findByUserIdOrderByStartTimeDesc(@Param("userId") Integer userId);
}