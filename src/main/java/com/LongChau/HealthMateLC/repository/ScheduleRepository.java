package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    
    // Method từ nhánh managercontroller - sử dụng Spring Data JPA naming convention
    List<Schedule> findByUser_UserId(Integer userId); // Lấy lịch theo UserID
    List<Schedule> findByUser_UserIdIn(List<Integer> userIds); // Sửa lại từ findByUserIdIn thành findByUser_UserIdIn

    // Methods từ nhánh dev - sử dụng @Query annotation
    @Query("SELECT s FROM Schedule s WHERE s.user.userId = :userId")
    List<Schedule> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT s FROM Schedule s WHERE s.date = :workDate")
    List<Schedule> findByWorkDate(@Param("workDate") LocalDate workDate);

    @Query("SELECT s FROM Schedule s WHERE s.user.userId = :userId AND s.date = :workDate")
    List<Schedule> findByUserIdAndWorkDate(@Param("userId") Integer userId, @Param("workDate") LocalDate workDate);
}