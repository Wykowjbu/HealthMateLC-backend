package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.config.RedirectConfig;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class Login {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedirectConfig redirectConfig;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepository.findUserByUsername(loginRequest.getUsername());
            if (user != null
                    && user.getPassword().equals(loginRequest.getPassword())
                    && user.getUsername().equals(loginRequest.getUsername())) {

                System.out.println("DEBUG: Setting session for user: " + user.getUsername());
                System.out.println("DEBUG: Session ID before setting: " + session.getId());

                // Chuẩn hóa role thành chữ thường
                String normalizedRole = user.getRole().toLowerCase();

                // Set session attributes
                session.setAttribute("currentUser", user);
                session.setAttribute("userRole", normalizedRole);
                session.setAttribute("userId", user.getUserId());

                // Set session timeout
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                System.out.println("DEBUG: Session attributes after setting:");
                System.out.println("DEBUG: currentUser: " + session.getAttribute("currentUser"));
                System.out.println("DEBUG: userRole: " + session.getAttribute("userRole"));
                System.out.println("DEBUG: userId: " + session.getAttribute("userId"));

                // Lấy redirect URL
                String redirectUrl = redirectConfig.getRedirectUrl(normalizedRole);
                System.out.println("DEBUG: Redirect URL: " + redirectUrl);

                response.put("redirectUrl", redirectUrl);
                response.put("success", true);
                response.put("message", "Đăng nhập thành công!");
                response.put("role", normalizedRole);
                response.put("sessionId", session.getId());

                return ResponseEntity.ok()
                        .header("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/; HttpOnly; SameSite=Lax; Domain=localhost")
                        .body(response);
            } else {
                response.put("success", false);
                response.put("message", "Tên Đăng Nhập Hoặc Mật Khẩu Không Chính Xác!");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Có Lỗi Xảy Ra: " + e.getMessage());
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
            response.put("redirectUrl", "/HealthMateLC/index.html"); // Chuyển hướng về trang đăng nhập

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
