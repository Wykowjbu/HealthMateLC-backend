package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.dto.EmployeeDTO;
import com.LongChau.HealthMateLC.dto.ScheduleDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.Schedule;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserHistory;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.service.ScheduleEmailService;
import com.LongChau.HealthMateLC.repository.UserRepository;
import com.LongChau.HealthMateLC.service.UserInformationService;
import com.LongChau.HealthMateLC.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.LongChau.HealthMateLC.dto.AttendanceDTO;
import com.LongChau.HealthMateLC.service.AttendanceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Time;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInformationService userInformationService;
    @Autowired
    private WorkScheduleService workScheduleService;
    @Autowired
    private ScheduleEmailService scheduleEmailService;

    @GetMapping("/profile")
    public ResponseEntity<?> getManagerProfile(
            HttpSession session,
            @RequestParam(value = "detail", required = false, defaultValue = "false") boolean detail) {

        System.out.println("DEBUG: Request to /manager/profile");
        System.out.println("DEBUG: Session ID from request: " + session.getId());
        System.out.println("DEBUG: Session is new: " + session.isNew());
        System.out.println("DEBUG: All session attributes: " + Collections.list(session.getAttributeNames()));

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            System.out.println("DEBUG: currentUser is NULL in session.");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }

        System.out
                .println("DEBUG: currentUser found: " + currentUser.getUsername() + ", Role: " + currentUser.getRole());

        if (!"MANAGER".equals(currentUser.getRole().toUpperCase())) {
            System.out.println("DEBUG: Invalid role: " + currentUser.getRole());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy vào");
            return ResponseEntity.status(403).body(errorResponse);
        }

        User manager = userRepository.findById(currentUser.getUserId()).orElse(null);
        if (manager == null) {
            System.out.println("DEBUG: Manager not found in DB for userId: " + currentUser.getUserId());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "Không tìm thấy thông tin người dùng");
            return ResponseEntity.status(404).body(errorResponse);
        }

        System.out.println("DEBUG: Manager found in DB: " + manager.getUsername());
        UserInformation userInfo = userInformationService.findUserInformationByUserId(manager.getUserId());

        Map<String, Object> profile = new HashMap<>();

        if (userInfo != null) {
            profile.put("fullName", userInfo.getFullName());
            if (userInfo.getPharmacy() != null) {
                profile.put("pharmacyName", userInfo.getPharmacy().getPharmacyName());
                profile.put("branch", userInfo.getPharmacy().getPharmacyName());
            } else {
                profile.put("pharmacyName", "Chưa gán chi nhánh");
                profile.put("branch", "Chưa gán chi nhánh");
            }
        } else {
            profile.put("fullName", manager.getUsername());
            profile.put("pharmacyName", "Chưa gán chi nhánh");
            profile.put("branch", "Chưa gán chi nhánh");
        }

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/showprofile")
    public ResponseEntity<?> showManagerProfile(HttpSession session) {
        System.out.println("DEBUG: Request to /manager/showprofile");
        System.out.println("DEBUG: Session ID from request: " + session.getId());
        System.out.println("DEBUG: Session is new: " + session.isNew());
        System.out.println("DEBUG: All session attributes: " + Collections.list(session.getAttributeNames()));

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            System.out.println("DEBUG: currentUser is NULL in session.");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }

        System.out
                .println("DEBUG: currentUser found: " + currentUser.getUsername() + ", Role: " + currentUser.getRole());

        if (!"MANAGER".equals(currentUser.getRole().toUpperCase())) {
            System.out.println("DEBUG: Invalid role: " + currentUser.getRole());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy cập");
            return ResponseEntity.status(403).body(errorResponse);
        }

        User manager = userRepository.findById(currentUser.getUserId()).orElse(null);
        if (manager == null) {
            System.out.println("DEBUG: Manager not found in DB for userId: " + currentUser.getUserId());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "Không tìm thấy thông tin người dùng");
            return ResponseEntity.status(404).body(errorResponse);
        }

        System.out.println("DEBUG: Manager found in DB: " + manager.getUsername());
        UserInformation userInfo = userInformationService.findUserInformationByUserId(manager.getUserId());

        Map<String, Object> profile = new HashMap<>();

        if (userInfo != null) {
            profile.put("fullName", userInfo.getFullName());
            profile.put("phone", userInfo.getPhone() != null ? userInfo.getPhone() : "Chưa cập nhật");
            profile.put("email", userInfo.getEmail() != null ? userInfo.getEmail() : "Chưa cập nhật");
            if (userInfo.getPharmacy() != null) {
                Pharmacy pharmacy = userInfo.getPharmacy();
                profile.put("pharmacyName", pharmacy.getPharmacyName());
                profile.put("pharmacyId", pharmacy.getPharmacyId());
                profile.put("pharmacyAddress", pharmacy.getAddress() != null ? pharmacy.getAddress() : "Chưa cập nhật");
                profile.put("pharmacyPhone", pharmacy.getPhone() != null ? pharmacy.getPhone() : "Chưa cập nhật");
            } else {
                profile.put("pharmacyName", "Chưa gán chi nhánh");
                profile.put("pharmacyId", null);
                profile.put("pharmacyAddress", "Chưa gán chi nhánh");
                profile.put("pharmacyPhone", "Chưa gán chi nhánh");
            }
        } else {
            profile.put("fullName", manager.getUsername());
            profile.put("phone", "Chưa cập nhật");
            profile.put("email", "Chưa cập nhật");
            profile.put("pharmacyName", "Chưa gán chi nhánh");
            profile.put("pharmacyId", null);
            profile.put("pharmacyAddress", "Chưa gán chi nhánh");
            profile.put("pharmacyPhone", "Chưa gán chi nhánh");
        }

        System.out.println("DEBUG: Returning full profile: " + profile);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleDTO>> getSchedules(
            HttpSession session,
            @RequestParam(value = "userId", required = false) Integer userId) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"manager".equals(currentUser.getRole().toLowerCase())) {
            return ResponseEntity.status(403).body(null);
        }
        UserInformation userInfo = userInformationService.findUserInformationByUserId(currentUser.getUserId());
        if (userInfo == null || userInfo.getPharmacy() == null) {
            return ResponseEntity.status(404).body(null);
        }
        List<ScheduleDTO> schedules;
        if (userId != null) {
            // Kiểm tra xem nhân viên có thuộc nhà thuốc của manager không
            User employee = userRepository.findById(userId).orElse(null);
            if (employee == null || employee.getUserInformation() == null ||
                    !userInfo.getPharmacy().getPharmacyId()
                            .equals(employee.getUserInformation().getPharmacy().getPharmacyId())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid user");
                errorResponse.put("message", "Nhân viên không thuộc nhà thuốc của bạn");
                return ResponseEntity.status(403).body((List<ScheduleDTO>) errorResponse);
            }
            schedules = workScheduleService.getSchedulesByUserId(userId); // Sử dụng phương thức mới
        } else {
            Integer pharmacyId = userInfo.getPharmacy().getPharmacyId();
            List<User> employees = userRepository.findEmployeesByPharmacyId(pharmacyId);
            List<Integer> employeeIds = employees.stream().map(User::getUserId).collect(Collectors.toList());
            schedules = workScheduleService.getSchedulesByUserIds(employeeIds);
        }
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/schedule")
    public ResponseEntity<?> updateSchedule(
            @RequestParam Integer scheduleId,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"manager".equals(currentUser.getRole().toLowerCase())) {
            return ResponseEntity.status(403).body(null);
        }

        try {
            // Lấy tên manager để gửi email
            UserInformation managerInfo = userInformationService.findUserInformationByUserId(currentUser.getUserId());
            String managerName = managerInfo != null && managerInfo.getFullName() != null ? managerInfo.getFullName()
                    : currentUser.getUsername();

            // Sử dụng method mới có gửi email
            Schedule schedule = workScheduleService.updateScheduleWithNotification(
                    scheduleId, Date.valueOf(date), Time.valueOf(startTime), Time.valueOf(endTime), managerName);

            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update schedule");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/schedule")
    public ResponseEntity<?> deleteSchedule(
            @RequestParam Integer scheduleId,
            HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"manager".equals(currentUser.getRole().toLowerCase())) {
            return ResponseEntity.status(403).body(null);
        }
        try {
            workScheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.ok().body(Map.of("message", "Schedule deleted successfully"));
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete schedule");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"manager".equals(currentUser.getRole().toLowerCase())) {
            return ResponseEntity.status(403).body(null);
        }
        UserInformation userInfo = userInformationService.findUserInformationByUserId(currentUser.getUserId());
        if (userInfo == null || userInfo.getPharmacy() == null) {
            return ResponseEntity.status(404).body(null); // Manager chưa được gán nhà thuốc
        }
        Integer pharmacyId = userInfo.getPharmacy().getPharmacyId();
        List<User> employees = userRepository.findEmployeesByPharmacyId(pharmacyId);
        List<EmployeeDTO> dtoList = employees.stream().map(user -> {
            EmployeeDTO dto = new EmployeeDTO();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            if (user.getUserInformation() != null) {
                dto.setFullName(user.getUserInformation().getFullName());
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> createSchedule(
            @RequestParam Integer userId,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"manager".equals(currentUser.getRole().toLowerCase())) {
            return ResponseEntity.status(403).body(null);
        }
        try {
            Schedule schedule = workScheduleService.createSchedule(
                    userId, Date.valueOf(date), Time.valueOf(startTime), Time.valueOf(endTime));
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create schedule");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Thêm method mới vào cuối class
    @GetMapping("/export-attendance")
    public ResponseEntity<?> exportAttendance(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"manager".equals(currentUser.getRole().toLowerCase())) {
            return ResponseEntity.status(403).body(null);
        }

        UserInformation userInfo = userInformationService.findUserInformationByUserId(currentUser.getUserId());
        if (userInfo == null || userInfo.getPharmacy() == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "No pharmacy assigned");
            errorResponse.put("message", "Manager chưa được gán nhà thuốc");
            return ResponseEntity.status(404).body(errorResponse);
        }

        try {
            Integer pharmacyId = userInfo.getPharmacy().getPharmacyId();
            String pharmacyName = userInfo.getPharmacy().getPharmacyName();

            LocalDate startDateLocal = LocalDate.parse(startDate);
            LocalDate endDateLocal = LocalDate.parse(endDate);

            List<AttendanceDTO> attendanceList = attendanceService.getAttendanceByPharmacyAndDateRange(
                    pharmacyId, startDateLocal, endDateLocal);

            byte[] excelData = attendanceService.generateAttendanceExcel(
                    attendanceList, pharmacyName, startDateLocal, endDateLocal);

            DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String fileName = String.format("BangChamCong_%s_%s_%s.xlsx",
                    pharmacyName.replaceAll("\\s+", ""),
                    startDateLocal.format(fileFormatter),
                    endDateLocal.format(fileFormatter));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (Exception e) {
            System.err.println("Error exporting attendance: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Export failed");
            errorResponse.put("message", "Không thể xuất file Excel: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

//#region History work employee
    // Lịch sử làm việc tất cả nhân viên thuộc nhà thuốc của manager
    @GetMapping("/history/all")
    public ResponseEntity<?> getAllEmployeeHistory(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"manager".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).body(null);
        }
        UserInformation userInfo = userInformationService.findUserInformationByUserId(currentUser.getUserId());
        if (userInfo == null || userInfo.getPharmacy() == null) {
            return ResponseEntity.status(404).body(null);
        }
        Integer pharmacyId = userInfo.getPharmacy().getPharmacyId();
        // Lấy tất cả nhân viên thuộc nhà thuốc
        List<User> employees = userRepository.findEmployeesByPharmacyId(pharmacyId);
        List<Integer> employeeIds = employees.stream().map(User::getUserId).collect(Collectors.toList());
        // Lấy lịch sử làm việc của các nhân viên này
        List<UserHistory> histories = workScheduleService.getUserHistoriesByUserIds(employeeIds);
        // Map sang DTO cho frontend
        List<Map<String, Object>> result = histories.stream().map(h -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("historyId", h.getHistoryId());
            dto.put("userId", h.getUser().getUserId());
            dto.put("fullName", h.getUser().getUserInformation() != null ? h.getUser().getUserInformation().getFullName() : h.getUser().getUsername());
            dto.put("pharmacyId", h.getPharmacy().getPharmacyId());
            dto.put("pharmacyName", h.getPharmacy().getPharmacyName());
            dto.put("startTime", h.getStartTime());
            dto.put("endTime", h.getEndTime());
            return dto;
        })
        .collect(Collectors.toList()); // Sửa lỗi toList() thành collect(Collectors.toList())
        return ResponseEntity.ok(result);
    }
    //#endregion
}