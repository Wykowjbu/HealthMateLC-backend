package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.config.RedirectConfig;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class Login {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedirectConfig redirectConfig;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            if (username == null || password == null) {
                response.put("success", false);
                response.put("message", "Tên đăng nhập hoặc mật khẩu không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Tên đăng nhập không tồn tại");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userOpt.get();
            if (password.equals(user.getPassword())) { // So sánh plain text tạm thời
                // Set currentUser vào session
                session.setAttribute("currentUser", user);

                // Lấy thông tin bổ sung từ UserInformation nếu có
                String fullName = null;
                String email = null;
                String phone = null;
                if (user.getUserInformation() != null) {
                    fullName = user.getUserInformation().getFullName();
                    email = user.getUserInformation().getEmail();
                    phone = user.getUserInformation().getPhone();
                }

                response.put("redirectUrl", redirectConfig.getRedirectUrl(user.getRole()));
                response.put("success", true);
                response.put("message", "Đăng nhập thành công!");
                response.put("userId", user.getUserId());
                response.put("username", user.getUsername());
                response.put("role", user.getRole());
                response.put("fullName", fullName);
                response.put("email", email);
                response.put("phone", phone);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("DEBUG: Attempting logout. Session ID: " + session.getId());

            // Kiểm tra xem session có tồn tại không
            if (session != null) {
                // Xóa các thuộc tính session
                session.removeAttribute("currentUser");
                session.removeAttribute("userRole");
                session.removeAttribute("userId");

                // Hủy phiên
                session.invalidate();

                System.out.println("DEBUG: Session invalidated successfully.");
            }

            response.put("success", true);
            response.put("message", "Đăng xuất thành công!");
            response.put("redirectUrl", "index.html"); // Chuyển hướng về trang đăng nhập

            return ResponseEntity.ok()
                    .header("Set-Cookie", "JSESSIONID=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax; Domain=localhost")
                    .body(response);
        } catch (Exception e) {
            System.err.println("Logout error: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Có Lỗi Xảy Ra Khi Đăng Xuất: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
