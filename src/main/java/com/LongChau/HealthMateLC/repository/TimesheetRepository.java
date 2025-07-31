package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.LongChau.HealthMateLC.dto.AttendanceDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Integer> {
    // ❌ REMOVE THIS PROBLEMATIC METHOD - Causes "not unique result" error
    // Optional<Timesheet> findByUser_UserIdAndDate(Integer userId, LocalDate date);

    // ✅ Works fine - no changes needed
    @Query("SELECT t FROM Timesheet t WHERE t.user.userId = :userId AND t.date = :date ORDER BY t.checkin ASC")
    List<Timesheet> findAllByUserIdAndDateOrderByCheckin(@Param("userId") Integer userId, @Param("date") LocalDate date);

    // ✅ Works fine - no changes needed
    @Query(value = "SELECT * FROM timesheet WHERE UserID = :userId AND date = :date AND " +
            "CAST(Checkin AS TIME) >= CAST(:startTime AS TIME) AND " +
            "CAST(Checkin AS TIME) <= CAST(:endTime AS TIME)", nativeQuery = true)
    List<Timesheet> findByUserIdDateAndCheckinRange(@Param("userId") Integer userId,
                                                    @Param("date") LocalDate date,
                                                    @Param("startTime") String startTime,
                                                    @Param("endTime") String endTime);

    // ✅ Works fine - no changes needed
    @Query(value = "SELECT TOP 1 * FROM timesheet WHERE UserID = :userId AND date = :date AND " +
            "Checkout IS NULL AND " +
            "CAST(Checkin AS TIME) >= CAST(:startTime AS TIME) AND " +
            "CAST(Checkin AS TIME) <= CAST(:endTime AS TIME)", nativeQuery = true)
    Optional<Timesheet> findIncompleteTimesheetForShift(@Param("userId") Integer userId,
                                                        @Param("date") LocalDate date,
                                                        @Param("startTime") String startTime,
                                                        @Param("endTime") String endTime);

    // ✅ FIXED: Return Integer instead of boolean
    @Query(value = "SELECT COUNT(*) FROM timesheet WHERE UserID = :userId AND date = :date AND " +
            "CAST(Checkin AS TIME) >= CAST(:startTime AS TIME) AND " +
            "CAST(Checkin AS TIME) <= CAST(:endTime AS TIME)", nativeQuery = true)
    Integer countByUserIdDateAndCheckinRange(@Param("userId") Integer userId,
                                             @Param("date") LocalDate date,
                                             @Param("startTime") String startTime,
                                             @Param("endTime") String endTime);

    // Thêm method này vào interface TimesheetRepository
    @Query("SELECT new com.LongChau.HealthMateLC.dto.AttendanceDTO(" +
            "t.timesheetId, t.user.userId, " +
            "COALESCE(ui.fullName, u.username), " +
            "t.date, t.checkin, t.checkout) " +
            "FROM Timesheet t " +
            "JOIN t.user u " +
            "LEFT JOIN u.userInformation ui " +
            "WHERE t.pharmacy.pharmacyId = :pharmacyId " +
            "AND t.date BETWEEN :startDate AND :endDate " +
            "ORDER BY t.date ASC, ui.fullName ASC")
    List<AttendanceDTO> findAttendanceByPharmacyAndDateRange(
            @Param("pharmacyId") Integer pharmacyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}