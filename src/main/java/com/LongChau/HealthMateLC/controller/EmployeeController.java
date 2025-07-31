package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.model.Schedule;
import com.LongChau.HealthMateLC.repository.CustomerRepository;
import com.LongChau.HealthMateLC.model.Invoice;
import com.LongChau.HealthMateLC.dto.ScheduleDTO;
import com.LongChau.HealthMateLC.model.*;
import com.LongChau.HealthMateLC.repository.TimesheetRepository;
import com.LongChau.HealthMateLC.repository.UserRepository;
import com.LongChau.HealthMateLC.dto.EmployeeInfoDTO;
import com.LongChau.HealthMateLC.dto.UserHistoryDTO;
import com.LongChau.HealthMateLC.dto.CreateOrderRequestDTO;
import com.LongChau.HealthMateLC.dto.InvoiceResponseDTO;
import com.LongChau.HealthMateLC.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private TimesheetRepository timesheetRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductsService productsService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private UserInformationService userInformationService;
    @Autowired
    private UserHistoryService userHistoryService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private WorkScheduleService workScheduleService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/profile")
    public ResponseEntity<?> getEmployeeProfile(
            HttpSession session,
            @RequestParam(value = "detail", required = false, defaultValue = "false") boolean detail) {

        System.out.println("DEBUG: Request to /employee/profile");
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }

        if (!"EMPLOYEE".equals(currentUser.getRole().toUpperCase())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy vào");
            return ResponseEntity.status(403).body(errorResponse);
        }

        User employee = userRepository.findById(currentUser.getUserId()).orElse(null);
        if (employee == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "Không tìm thấy thông tin người dùng");
            return ResponseEntity.status(404).body(errorResponse);
        }

        UserInformation userInfo = userInformationService.findUserInformationByUserId(employee.getUserId());
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
            profile.put("fullName", employee.getUsername());
            profile.put("pharmacyName", "Chưa gán chi nhánh");
            profile.put("branch", "Chưa gán chi nhánh");
        }

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/showprofile")
    public ResponseEntity<?> showEmployeeProfile(HttpSession session) {
        System.out.println("DEBUG: Request to /employee/showprofile");
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }

        if (!"EMPLOYEE".equals(currentUser.getRole().toUpperCase())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy cập");
            return ResponseEntity.status(403).body(errorResponse);
        }

        User employee = userRepository.findById(currentUser.getUserId()).orElse(null);
        if (employee == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "Không tìm thấy thông tin người dùng");
            return ResponseEntity.status(404).body(errorResponse);
        }

        UserInformation userInfo = userInformationService.findUserInformationByUserId(employee.getUserId());
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
            profile.put("fullName", employee.getUsername());
            profile.put("phone", "Chưa cập nhật");
            profile.put("email", "Chưa cập nhật");
            profile.put("pharmacyName", "Chưa gán chi nhánh");
            profile.put("pharmacyId", null);
            profile.put("pharmacyAddress", "Chưa gán chi nhánh");
            profile.put("pharmacyPhone", "Chưa gán chi nhánh");
        }

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/schedules")
    public ResponseEntity<?> getEmployeeSchedules(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"EMPLOYEE".equals(currentUser.getRole().toUpperCase())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        List<ScheduleDTO> schedules = workScheduleService.getSchedulesForEmployee(currentUser.getUserId());
        return ResponseEntity.ok(schedules);
    }

    // Đây là phương thức để check in và check out cho nhân viên
    // ✅ FIXED: Get timesheet status by shift
    @GetMapping("/timesheet/status-by-shift")
    public ResponseEntity<?> getTimesheetStatusByShift(HttpSession session) {
        System.out.println("DEBUG: Request to /employee/timesheet/status-by-shift");
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }

        if (!"EMPLOYEE".equals(currentUser.getRole().toUpperCase())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy cập thông tin này");
            return ResponseEntity.status(403).body(errorResponse);
        }

        try {
            Integer userId = currentUser.getUserId();
            LocalDate today = LocalDate.now();
            LocalTime now = LocalDateTime.now().toLocalTime();

            System.out.println("DEBUG: UserId: " + userId + ", Today: " + today + ", Now: " + now);

            // Get today's schedules
            List<ScheduleDTO> todaySchedules = workScheduleService.getSchedulesForEmployee(userId)
                    .stream()
                    .filter(s -> {
                        try {
                            return LocalDate.parse(s.getDate()).equals(today);
                        } catch (Exception e) {
                            System.out.println("DEBUG: Error parsing schedule date: " + s.getDate());
                            return false;
                        }
                    })
                    .toList();

            System.out.println("DEBUG: Found " + todaySchedules.size() + " schedules for today");

            // ✅ FIXED: Use new method that handles multiple timesheets
            List<Timesheet> todayTimesheets = timesheetRepository.findAllByUserIdAndDateOrderByCheckin(userId, today);
            System.out.println("DEBUG: Found " + todayTimesheets.size() + " timesheets for today");

            // Create response for each shift
            List<Map<String, Object>> shiftStatuses = new ArrayList<>();

            for (ScheduleDTO schedule : todaySchedules) {
                try {
                    Map<String, Object> shiftStatus = new HashMap<>();
                    shiftStatus.put("scheduleId", schedule.getScheduleId());
                    shiftStatus.put("startTime", schedule.getStartTime());
                    shiftStatus.put("endTime", schedule.getEndTime());
                    shiftStatus.put("shiftName", schedule.getStartTime() + " - " + schedule.getEndTime());

                    // Calculate allowed check-in times
                    LocalTime shiftStart = LocalTime.parse(schedule.getStartTime());
                    LocalTime shiftEnd = LocalTime.parse(schedule.getEndTime());
                    LocalTime allowedCheckInStart = shiftStart.minusMinutes(5);
                    LocalTime allowedCheckInEnd = shiftStart.plusMinutes(5);

                    shiftStatus.put("allowedCheckInStart", allowedCheckInStart.toString());
                    shiftStatus.put("allowedCheckInEnd", allowedCheckInEnd.toString());

                    // Check if can check-in now
                    boolean canCheckInNow = !now.isBefore(allowedCheckInStart) && !now.isAfter(allowedCheckInEnd);
                    shiftStatus.put("canCheckInNow", canCheckInNow);

                    // Check if can check-out now
                    boolean canCheckOutNow = !now.isBefore(shiftEnd);
                    shiftStatus.put("canCheckOutNow", canCheckOutNow);
                    shiftStatus.put("checkOutAllowedFrom", shiftEnd.toString());

                    // ✅ FIXED: Find matching timesheet for this specific shift
                    Timesheet matchingTimesheet = todayTimesheets.stream()
                            .filter(ts -> {
                                if (ts.getCheckin() == null)
                                    return false;
                                LocalTime checkinTime = ts.getCheckin();
                                // Check if checkin is within this shift's window
                                return !checkinTime.isBefore(allowedCheckInStart)
                                        && !checkinTime.isAfter(allowedCheckInEnd);
                            })
                            .findFirst()
                            .orElse(null);

                    if (matchingTimesheet != null) {
                        shiftStatus.put("checkInTime", matchingTimesheet.getCheckin().toString());
                        shiftStatus.put("checkOutTime",
                                matchingTimesheet.getCheckout() != null ? matchingTimesheet.getCheckout().toString()
                                        : null);

                        if (matchingTimesheet.getCheckout() != null) {
                            shiftStatus.put("status", "completed");
                        } else {
                            shiftStatus.put("status", "working");
                        }
                    } else {
                        shiftStatus.put("checkInTime", null);
                        shiftStatus.put("checkOutTime", null);
                        shiftStatus.put("status", "not_started");
                    }

                    shiftStatuses.add(shiftStatus);

                } catch (Exception e) {
                    System.out.println(
                            "DEBUG: Error processing schedule " + schedule.getScheduleId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("shifts", shiftStatuses);
            response.put("date", today.toString());
            response.put("currentTime", now.toString());

            System.out.println("DEBUG: Successfully returning " + shiftStatuses.size() + " shift statuses");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("DEBUG: General error in getTimesheetStatusByShift: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "Lỗi máy chủ: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // ✅ FIXED: Check-in for shift
    @PostMapping("/timesheet/check-in-shift")
    public ResponseEntity<?> checkInForShift(@RequestBody Map<String, Object> request, HttpSession session) {
        System.out.println("DEBUG: Request to /employee/timesheet/check-in-shift at " + LocalDateTime.now());

        try {
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Unauthorized access");
                errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
                return ResponseEntity.status(401).body(errorResponse);
            }

            if (!"EMPLOYEE".equals(currentUser.getRole().toUpperCase())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Unauthorized access");
                errorResponse.put("message", "Bạn không có quyền truy cập");
                return ResponseEntity.status(403).body(errorResponse);
            }

            Integer userId = currentUser.getUserId();
            Integer scheduleId = null;

            // Parse scheduleId from request
            try {
                Object scheduleIdObj = request.get("scheduleId");
                if (scheduleIdObj instanceof Number) {
                    scheduleId = ((Number) scheduleIdObj).intValue();
                } else if (scheduleIdObj instanceof String) {
                    scheduleId = Integer.parseInt((String) scheduleIdObj);
                }
                System.out.println("DEBUG: Parsed scheduleId: " + scheduleId);
            } catch (Exception e) {
                System.out.println("DEBUG: Error parsing scheduleId: " + e.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid schedule ID");
                errorResponse.put("message", "ID ca làm việc không hợp lệ: " + e.getMessage());
                return ResponseEntity.status(400).body(errorResponse);
            }

            if (scheduleId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid schedule");
                errorResponse.put("message", "ID ca làm việc không hợp lệ");
                return ResponseEntity.status(400).body(errorResponse);
            }

            LocalDate today = LocalDate.now();
            LocalTime now = LocalDateTime.now().toLocalTime();

            // Get schedule information
            Integer finalScheduleId = scheduleId;
            Optional<ScheduleDTO> scheduleOpt = workScheduleService.getSchedulesForEmployee(userId)
                    .stream()
                    .filter(s -> {
                        try {
                            return LocalDate.parse(s.getDate()).equals(today) &&
                                    s.getScheduleId().equals(finalScheduleId);
                        } catch (Exception e) {
                            System.out.println("DEBUG: Error filtering schedule: " + e.getMessage());
                            return false;
                        }
                    })
                    .findFirst();

            if (!scheduleOpt.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Schedule not found");
                errorResponse.put("message", "Không tìm thấy ca làm việc này cho hôm nay");
                return ResponseEntity.status(400).body(errorResponse);
            }

            ScheduleDTO schedule = scheduleOpt.get();

            // Parse shift times
            LocalTime shiftStart = LocalTime.parse(schedule.getStartTime());
            LocalTime shiftEnd = LocalTime.parse(schedule.getEndTime());
            LocalTime allowedCheckInStart = shiftStart.minusMinutes(5);
            LocalTime allowedCheckInEnd = shiftStart.plusMinutes(5);

            System.out.println("DEBUG: Current time: " + now);
            System.out.println("DEBUG: Shift start: " + shiftStart);
            System.out.println("DEBUG: Allowed check-in window: " + allowedCheckInStart + " to " + allowedCheckInEnd);

            // Validate check-in time
            if (now.isBefore(allowedCheckInStart)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Too early");
                errorResponse.put("message", "Chỉ có thể check-in từ " + allowedCheckInStart.toString() +
                        " (5 phút trước ca bắt đầu)");
                return ResponseEntity.status(400).body(errorResponse);
            }

            if (now.isAfter(allowedCheckInEnd)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Too late");
                errorResponse.put("message", "Đã quá thời gian check-in. Chỉ có thể check-in đến " +
                        allowedCheckInEnd.toString() + " (5 phút sau ca bắt đầu)");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // ✅ FIXED: Check if already checked in for this specific shift
            // ✅ FIXED: Convert LocalTime to String for SQL Server
            // Thay thế dòng này:
            String startTimeStr = allowedCheckInStart.toString();
            String endTimeStr = allowedCheckInEnd.toString();

            // ✅ FIXED: Use count method instead of boolean method
            Integer existingCount = timesheetRepository.countByUserIdDateAndCheckinRange(
                    userId, today, startTimeStr, endTimeStr);
            boolean alreadyCheckedIn = existingCount != null && existingCount > 0;

            System.out.println("DEBUG: Existing count: " + existingCount + ", Already checked in: " + alreadyCheckedIn);

            if (alreadyCheckedIn) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Already checked in");
                errorResponse.put("message", "Bạn đã check-in cho ca này rồi!");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // Create new timesheet
            Timesheet timesheet = new Timesheet();
            timesheet.setUser(currentUser);

            // Set pharmacy safely
            try {
                if (currentUser.getUserInformation() != null
                        && currentUser.getUserInformation().getPharmacy() != null) {
                    timesheet.setPharmacy(currentUser.getUserInformation().getPharmacy());
                } else {
                    System.out.println("DEBUG: No pharmacy found for user, setting null");
                    timesheet.setPharmacy(null);
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Error setting pharmacy: " + e.getMessage());
                timesheet.setPharmacy(null);
            }

            timesheet.setCheckin(now);
            timesheet.setDate(today);

            // Save timesheet
            timesheetRepository.save(timesheet);
            System.out.println("DEBUG: Check-in saved successfully for user: " + userId + ", shift: " + scheduleId);

            Map<String, Object> response = new HashMap<>();
            response.put("message",
                    "Check-in thành công cho ca " + schedule.getStartTime() + " - " + schedule.getEndTime());
            response.put("checkInTime", now.toString());
            response.put("scheduleId", scheduleId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("DEBUG: General error in checkInForShift: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "Lỗi máy chủ: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // ✅ FIXED: Check-out for shift
    @PostMapping("/timesheet/check-out-shift")
    public ResponseEntity<?> checkOutForShift(@RequestBody Map<String, Object> request, HttpSession session) {
        System.out.println("DEBUG: Request to /employee/timesheet/check-out-shift at " + LocalDateTime.now());

        try {
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Unauthorized access");
                errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
                return ResponseEntity.status(401).body(errorResponse);
            }

            if (!"EMPLOYEE".equals(currentUser.getRole().toUpperCase())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Unauthorized access");
                errorResponse.put("message", "Bạn không có quyền truy cập");
                return ResponseEntity.status(403).body(errorResponse);
            }

            Integer userId = currentUser.getUserId();
            Integer scheduleId = null;

            // Parse scheduleId from request
            try {
                Object scheduleIdObj = request.get("scheduleId");
                if (scheduleIdObj instanceof Number) {
                    scheduleId = ((Number) scheduleIdObj).intValue();
                } else if (scheduleIdObj instanceof String) {
                    scheduleId = Integer.parseInt((String) scheduleIdObj);
                }
                System.out.println("DEBUG: Parsed scheduleId: " + scheduleId);
            } catch (Exception e) {
                System.out.println("DEBUG: Error parsing scheduleId: " + e.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid schedule ID");
                errorResponse.put("message", "ID ca làm việc không hợp lệ: " + e.getMessage());
                return ResponseEntity.status(400).body(errorResponse);
            }

            if (scheduleId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid schedule");
                errorResponse.put("message", "ID ca làm việc không hợp lệ");
                return ResponseEntity.status(400).body(errorResponse);
            }

            LocalDate today = LocalDate.now();
            LocalTime now = LocalDateTime.now().toLocalTime();

            // Get schedule information
            Integer finalScheduleId = scheduleId;
            Optional<ScheduleDTO> scheduleOpt = workScheduleService.getSchedulesForEmployee(userId)
                    .stream()
                    .filter(s -> {
                        try {
                            return LocalDate.parse(s.getDate()).equals(today) &&
                                    s.getScheduleId().equals(finalScheduleId);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .findFirst();

            if (!scheduleOpt.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Schedule not found");
                errorResponse.put("message", "Không tìm thấy ca làm việc này");
                return ResponseEntity.status(400).body(errorResponse);
            }

            ScheduleDTO schedule = scheduleOpt.get();
            LocalTime shiftStart = LocalTime.parse(schedule.getStartTime());
            LocalTime shiftEnd = LocalTime.parse(schedule.getEndTime());

            System.out.println("DEBUG: Current time: " + now);
            System.out.println("DEBUG: Shift end time: " + shiftEnd);

            // Check if it's time to check-out
            if (now.isBefore(shiftEnd)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Too early for checkout");
                errorResponse.put("message", "Chỉ có thể check-out sau " + shiftEnd.toString() +
                        " (giờ tan làm của ca)");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // ✅ FIXED: Find the specific incomplete timesheet for this shift
            LocalTime allowedCheckInStart = shiftStart.minusMinutes(5);
            LocalTime allowedCheckInEnd = shiftStart.plusMinutes(5);

            // ✅ FIXED: Convert LocalTime to String for SQL Server
            String startTimeStr = allowedCheckInStart.toString();
            String endTimeStr = allowedCheckInEnd.toString();

            Optional<Timesheet> matchingTimesheetOpt = timesheetRepository.findIncompleteTimesheetForShift(
                    userId, today, startTimeStr, endTimeStr);

            if (!matchingTimesheetOpt.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "No check-in found");
                errorResponse.put("message", "Vui lòng check-in trước khi check-out!");
                return ResponseEntity.status(400).body(errorResponse);
            }

            Timesheet matchingTimesheet = matchingTimesheetOpt.get();

            // Update check-out time
            matchingTimesheet.setCheckout(now);
            timesheetRepository.save(matchingTimesheet);
            System.out.println("DEBUG: Check-out saved for user: " + userId + ", shift: " + scheduleId);

            Map<String, Object> response = new HashMap<>();
            response.put("message",
                    "Check-out thành công cho ca " + schedule.getStartTime() + " - " + schedule.getEndTime());
            response.put("checkOutTime", now.toString());
            response.put("scheduleId", scheduleId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("DEBUG: General error in checkOutForShift: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "Lỗi máy chủ: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


    @GetMapping("/danh-sach-khach-hang")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAll();
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/tao-moi-khach-hang")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        if (isPhoneOrEmailExists(customer)) {
            return ResponseEntity.badRequest().body(customer);
        }
        Customer addNewCustomer = customerService.addNewCustomer(customer);
        return ResponseEntity.ok(addNewCustomer);
    }

    @PutMapping("/cap-nhat-khach-hang/{id}")
    public ResponseEntity<Customer> updateCustomerById(@PathVariable Integer id, @RequestBody Customer customer) {
        // Set the customer ID from path variable
        customer.setCustomerId(id);
        if (isPhoneOrEmailExists(customer)) {
            return ResponseEntity.badRequest().body(customer);
        }
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    public boolean isPhoneOrEmailExists(Customer customer) {
        List<Customer> customers = customerService.getAll();
        for (Customer existingCustomer : customers) {
            if (!existingCustomer.getCustomerId().equals(customer.getCustomerId())) {
                if (existingCustomer.getPhone() != null && existingCustomer.getPhone().equals(customer.getPhone())) {
                    return true;
                }
                if (existingCustomer.getEmail() != null && existingCustomer.getEmail().equals(customer.getEmail())) {
                    return true;
                }
            }
        }
        return false;
    }

    @GetMapping("/danh-sach-san-pham")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> list = productsService.getAll();
        return ResponseEntity.ok(list);
        // phanhuy
    }

    @GetMapping("/lich-lam-viec")
    public ResponseEntity<List<Schedule>> getScheduleByUserId(@RequestParam Integer userId) {
        List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/lich-su-don-hang")
    public ResponseEntity<List<Invoice>> getOrderHistoryByCustomerId(@RequestParam Integer customerId) {
        List<Invoice> orderHistory = invoiceService.getOrderHistoryByCustomerId(customerId);
        return ResponseEntity.ok(orderHistory);
    }

    @GetMapping("/danh-sach-don-hang")
    public ResponseEntity<List<InvoiceResponseDTO>> getTodayInvoices() {
        List<InvoiceResponseDTO> todayInvoices = invoiceService.getAllInvoicesWithDetails();
        System.out.println(todayInvoices);
        return ResponseEntity.ok(todayInvoices);
    }

    @GetMapping("/thong-tin-nhan-vien/{id}")
    public ResponseEntity<EmployeeInfoDTO> getEmployeeInfo(@PathVariable Integer id) {
        EmployeeInfoDTO employeeInfo = userInformationService.getEmployeeInfoById(id);

        if (employeeInfo != null) {
            return ResponseEntity.ok(employeeInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lich-su-cong-tac/{userId}")
    public ResponseEntity<List<UserHistoryDTO>> getUserWorkHistory(@PathVariable Integer userId) {
        List<UserHistoryDTO> workHistory = userHistoryService.getUserWorkHistoryByUserId(userId);
        return ResponseEntity.ok(workHistory);
    }

    @PostMapping("/tao-don-hang")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequestDTO orderRequest) {
        System.out.println(orderRequest.getStatus() + " thong tin o day ---------------------------------------------");
        System.out.println(orderRequest);
        try {
            InvoiceResponseDTO createdInvoice = invoiceService.createOrder(orderRequest);
            // ==============================================================
            // Sau khi tạo đơn hàng thành công, gửi email cho khách hàng
            // ==============================================================

            String customerEmail = customerService.getCustomerById(createdInvoice.getCustomerId()).getEmail();
            String takeNote = createdInvoice.getNotes(); // đợi Huy update
            System.out.println("Customer email: " + customerEmail);
            String surveyLink = "https://wykowjbu.github.io/HealthMateLC/survey?invoiceId=" + createdInvoice.getInvoiceId();
            String subject = "Thông tin đơn hàng và khảo sát từ Long Châu";
            String content = "Cảm ơn bạn đã mua hàng tại Long Châu!\n\nHướng dẫn sử dụng: "
                    + (takeNote != null ? takeNote : "Không có hướng dẫn") +
                    "\n\nVui lòng dành chút thời gian để hoàn thành khảo sát dịch vụ tại đây: " + surveyLink;
            if (customerEmail != null && !customerEmail.isEmpty()) {
                try {
                    emailService.sendSimpleEmail(customerEmail, subject, content);
                } catch (Exception e) {
                    System.err.println("Lỗi gửi email sau khi tạo đơn hàng: " + e.getMessage());
                }
            }
            // ==============================================================
            return ResponseEntity.ok(createdInvoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating order123: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error occurred");
        }
    }

    // #region Get employee work history
    @GetMapping("/history")
    public ResponseEntity<?> getEmployeeWorkHistory(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        Integer userId = currentUser.getUserId();
        try {
            List<UserHistory> workHistory = workScheduleService.getWorkHistoryForEmployee(userId);
            // Map to DTO for frontend
            List<Map<String, Object>> result = workHistory.stream().map(h -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("historyId", h.getHistoryId());
                dto.put("pharmacyName", h.getPharmacy().getPharmacyName());
                // Format date only (yyyy-MM-dd)
                String startDate = h.getStartTime() != null ? h.getStartTime().toLocalDate().toString() : "";
                String endDate = h.getEndTime() != null ? h.getEndTime().toLocalDate().toString() : "";
                dto.put("startDate", startDate);
                dto.put("endDate", endDate);
                return dto;
            }).collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "Lỗi máy chủ: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventoryList = inventoryService.getAll();

        return ResponseEntity.ok(inventoryList);
    }

    // #endregion
}