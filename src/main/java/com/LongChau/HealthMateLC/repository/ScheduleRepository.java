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

    @Query("SELECT s FROM Schedule s WHERE s.user.userId = :userId")
    List<Schedule> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT s FROM Schedule s WHERE s.workDate = :workDate")
    List<Schedule> findByWorkDate(@Param("workDate") LocalDate workDate);

    @Query("SELECT s FROM Schedule s WHERE s.user.userId = :userId AND s.workDate = :workDate")
    List<Schedule> findByUserIdAndWorkDate(@Param("userId") Integer userId, @Param("workDate") LocalDate workDate);
}
