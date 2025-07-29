package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.ScheduleDTO;
import com.LongChau.HealthMateLC.model.Schedule;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserHistory;
import com.LongChau.HealthMateLC.repository.ScheduleRepository;
import com.LongChau.HealthMateLC.repository.UserHistoryRepository;
import com.LongChau.HealthMateLC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing work schedules.
 */
@Service
public class WorkScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @Autowired
    private ScheduleEmailService scheduleEmailService;

    /**
     * Converts a Schedule entity to a ScheduleDTO.
     * 
     * @param schedule the schedule entity
     * @return the corresponding DTO
     */
    private ScheduleDTO convertToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setUserId(schedule.getUser().getUserId());
        dto.setFullName(schedule.getUser().getUserInformation() != null
                ? schedule.getUser().getUserInformation().getFullName()
                : schedule.getUser().getUsername());
        dto.setDate(schedule.getDate().toString());
        dto.setStartTime(schedule.getStartTime().toString());
        dto.setEndTime(schedule.getEndTime().toString());
        if (schedule.getUser().getUserInformation() != null
                && schedule.getUser().getUserInformation().getPharmacy() != null) {
            dto.setPharmacyId(schedule.getUser().getUserInformation().getPharmacy().getPharmacyId());
            dto.setPharmacyName(schedule.getUser().getUserInformation().getPharmacy().getPharmacyName());
        }
        return dto;
    }

    /**
     * Retrieves schedules for a single user ID (aliased for EmployeeController).
     * 
     * @param userId the ID of the user
     * @return list of schedule DTOs
     */
    public List<ScheduleDTO> getSchedulesForEmployee(Integer userId) {
        return getSchedulesByUserId(userId); // Gọi lại phương thức hiện có
    }

    /**
     * Retrieves schedules for a list of user IDs.
     * 
     * @param userIds list of user IDs
     * @return list of schedule DTOs
     */
    public List<ScheduleDTO> getSchedulesByUserIds(List<Integer> userIds) {
        List<Schedule> schedules = scheduleRepository.findByUser_UserIdIn(userIds);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves schedules for a single user ID.
     * 
     * @param userId the ID of the user
     * @return list of schedule DTOs
     */
    public List<ScheduleDTO> getSchedulesByUserId(Integer userId) {
        List<Schedule> schedules = scheduleRepository.findByUser_UserId(userId);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Creates a new schedule for a user.
     * 
     * @param userId    the ID of the user
     * @param date      the schedule date
     * @param startTime the start time
     * @param endTime   the end time
     * @return the saved schedule
     * @throws IllegalArgumentException if user is not found
     */
    public Schedule createSchedule(Integer userId, Date date, Time startTime, Time endTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        Schedule schedule = new Schedule();
        schedule.setUser(user);
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        return scheduleRepository.save(schedule);
    }

    /**
     * Deletes a schedule by ID.
     * 
     * @param scheduleId the schedule ID to delete
     * @throws IllegalArgumentException if schedule is not found
     */
    public void deleteSchedule(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
        scheduleRepository.delete(schedule);
    }

    /**
     * Updates a schedule (original method - no email notification).
     * 
     * @param scheduleId the schedule ID
     * @param date       the new date
     * @param startTime  the new start time
     * @param endTime    the new end time
     * @return the updated schedule
     * @throws IllegalArgumentException if schedule is not found
     */
    public Schedule updateSchedule(Integer scheduleId, Date date, Time startTime, Time endTime) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        return scheduleRepository.save(schedule);
    }

    /**
     * Updates a schedule with email notification to employee.
     * 
     * @param scheduleId  the schedule ID
     * @param date        the new date
     * @param startTime   the new start time
     * @param endTime     the new end time
     * @param managerName the name of the manager making the change
     * @return the updated schedule
     * @throws RuntimeException if schedule is not found or update fails
     */
    public Schedule updateScheduleWithNotification(Integer scheduleId, Date date, Time startTime, Time endTime,
            String managerName) {
        try {
            // Lấy thông tin lịch cũ trước khi cập nhật
            Schedule oldSchedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch làm việc với ID: " + scheduleId));

            // Tạo bản sao của lịch cũ để gửi email
            Schedule oldScheduleCopy = new Schedule();
            oldScheduleCopy.setScheduleId(oldSchedule.getScheduleId());
            oldScheduleCopy.setDate(oldSchedule.getDate());
            oldScheduleCopy.setStartTime(oldSchedule.getStartTime());
            oldScheduleCopy.setEndTime(oldSchedule.getEndTime());
            oldScheduleCopy.setUser(oldSchedule.getUser());

            // Lưu thông tin nhân viên
            User employee = oldSchedule.getUser();

            // Cập nhật lịch mới
            oldSchedule.setDate(date);
            oldSchedule.setStartTime(startTime);
            oldSchedule.setEndTime(endTime);

            Schedule updatedSchedule = scheduleRepository.save(oldSchedule);

            // Gửi email thông báo (async để không block)
            try {
                scheduleEmailService.sendScheduleUpdateNotification(
                        employee,
                        oldScheduleCopy,
                        updatedSchedule,
                        managerName);
                System.out.println("📧 Đã gửi email thông báo cập nhật lịch cho nhân viên ID: " + employee.getUserId());
            } catch (Exception emailError) {
                System.err.println("⚠️ Lịch đã được cập nhật nhưng có lỗi khi gửi email: " + emailError.getMessage());
                // Không throw exception để không ảnh hưởng việc cập nhật lịch
            }

            return updatedSchedule;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật lịch làm việc: " + e.getMessage());
            throw new RuntimeException("Lỗi khi cập nhật lịch làm việc: " + e.getMessage());
        }
    }

    //#region History work employee
    /**
     * 
     * Lấy lịch sử làm việc của nhiều nhân viên theo danh sách userId
     */
    public List<UserHistory> getUserHistoriesByUserIds(List<Integer> userIds) {
        return userHistoryRepository.findByUser_UserIdIn(userIds);
    }
    //#endregion
}