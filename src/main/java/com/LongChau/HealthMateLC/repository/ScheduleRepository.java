package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.dto.ScheduleDTO;
import com.LongChau.HealthMateLC.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByUser_UserId(Integer userId); // Lấy lịch theo UserID
    List<Schedule> findByUser_UserIdIn(List<Integer> userIds); // Sửa lại từ findByUserIdIn thành findByUser_UserIdIn

}
