package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.service.EmailService;
import com.LongChau.HealthMateLC.service.OtpStorage;
import com.LongChau.HealthMateLC.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class ResetPasswordController {
    private final OtpStorage otpStorage;
    private final EmailService emailService;
    private final PasswordService passwordService;

    public ResetPasswordController(OtpStorage otpStorage, EmailService emailService, PasswordService passwordService) {
        this.otpStorage = otpStorage;
        this.emailService = emailService;
        this.passwordService = passwordService;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestParam String username) {
        System.out.println("Received request-otp for username: " + username);
        Optional<User> userOpt = passwordService.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("User not found for: " + username);
            return ResponseEntity.badRequest().body("Username không tồn tại");
        }
        User user = userOpt.get();
        if (user.getUserInformation() == null) {
            System.out.println("UserInformation is null for: " + username);
            return ResponseEntity.badRequest().body("Email không tồn tại");
        }
        String email = user.getUserInformation().getEmail();
        System.out.println("Email retrieved: " + email);
        String otp = String.format("%06d", new Random().nextInt(999999));
        System.out.println("OTP generated: " + otp);
        otpStorage.storeOtp(username, otp);
        System.out.println("Attempting to send OTP to: " + email);
        try {
            emailService.sendOtpEmail(email, otp);
            System.out.println("OTP sent successfully to: " + email);
            return ResponseEntity.ok("OTP đã gửi"); // Đảm bảo trả về 200
        } catch (Exception e) {
            System.err.println("Error sending OTP: " + e.getMessage());
            return ResponseEntity.status(500).body("Lỗi gửi OTP: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        if (otpStorage.verifyOtp(username, otp)) {
            return ResponseEntity.ok("OTP hợp lệ");
        }
        return ResponseEntity.badRequest().body("OTP không hợp lệ");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String username, @RequestParam String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Mật khẩu không hợp lệ");
        }
        Optional<User> userOpt = passwordService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Username không tồn tại");
        }
        User user = userOpt.get();
        user.setPassword(newPassword); // Lưu plain text tạm thời
        passwordService.changePassword(username, newPassword); // Cập nhật database
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}
