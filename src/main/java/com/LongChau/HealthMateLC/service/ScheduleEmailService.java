package com.LongChau.HealthMateLC.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.Schedule;
import java.time.format.DateTimeFormatter;

@Service
public class ScheduleEmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UserInformationService userInformationService;

    public void sendScheduleUpdateNotification(User employee, Schedule oldSchedule, Schedule newSchedule, String managerName) {
        try {
            // Lấy email từ UserInformation
            String employeeEmail = getEmployeeEmailFromUserInformation(employee.getUserId());

            if (employeeEmail == null || employeeEmail.trim().isEmpty()) {
                System.err.println("❌ Không tìm thấy email cho nhân viên: " + getEmployeeName(employee) + " (ID: " + employee.getUserId() + ")");
                return;
            }

            // Validate email format
            if (!isValidEmail(employeeEmail)) {
                System.err.println("❌ Email không hợp lệ cho nhân viên: " + employeeEmail);
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(employeeEmail);
            message.setFrom("noreply@longchau.com"); // Thay bằng email sender đã cấu hình của bạn
            message.setSubject("📅 THÔNG BÁO THAY ĐỔI LỊCH LÀM VIỆC - Long Châu");

            // Tạo nội dung email
            String content = createEmailContent(employee, oldSchedule, newSchedule, managerName);
            message.setText(content);

            emailSender.send(message);
            System.out.println("✅ Email thông báo lịch đã được gửi thành công đến: " + employeeEmail);

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi email thông báo lịch: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để không ảnh hưởng việc cập nhật lịch
        }
    }

    private String getEmployeeEmailFromUserInformation(Integer userId) {
        try {
            var userInfo = userInformationService.findUserInformationByUserId(userId);
            if (userInfo != null && userInfo.getEmail() != null) {
                return userInfo.getEmail().trim();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy email từ UserInformation: " + e.getMessage());
        }
        return null;
    }

    private String createEmailContent(User employee, Schedule oldSchedule, Schedule newSchedule, String managerName) {
        StringBuilder content = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String employeeName = getEmployeeName(employee);

        content.append("Kính chào anh/chị ").append(employeeName).append(",\n\n");
        content.append("Lịch làm việc của anh/chị đã được cập nhật bởi ").append(managerName).append(".\n\n");

        content.append("📋 THÔNG TIN THAY ĐỔI:\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Lịch cũ
        content.append("🔸 LỊCH CŨ:\n");
        content.append("   • Ngày: ").append(oldSchedule.getDate().toLocalDate().format(dateFormatter)).append("\n");
        content.append("   • Giờ: ").append(oldSchedule.getStartTime().toLocalTime().format(timeFormatter))
                .append(" - ").append(oldSchedule.getEndTime().toLocalTime().format(timeFormatter)).append("\n\n");

        // Lịch mới
        content.append("🔸 LỊCH MỚI:\n");
        content.append("   • Ngày: ").append(newSchedule.getDate().toLocalDate().format(dateFormatter)).append("\n");
        content.append("   • Giờ: ").append(newSchedule.getStartTime().toLocalTime().format(timeFormatter))
                .append(" - ").append(newSchedule.getEndTime().toLocalTime().format(timeFormatter)).append("\n\n");

        content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("📞 Liên hệ: Nếu có thắc mắc, vui lòng liên hệ với quản lý ").append(managerName).append("\n\n");

        content.append("⏰ Thời gian thông báo: ").append(java.time.LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");

        content.append("Trân trọng,\n");
        content.append("Hệ thống quản lý Long Châu\n");
        content.append("🌐 Website: longchau.com\n");
        content.append("📧 Email: support@longchau.com");

        return content.toString();
    }

    private String getEmployeeName(User employee) {
        try {
            if (employee.getUserInformation() != null &&
                    employee.getUserInformation().getFullName() != null) {
                return employee.getUserInformation().getFullName();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy tên nhân viên: " + e.getMessage());
        }
        return employee.getUsername();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}