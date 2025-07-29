package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.config.RedirectConfig;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.repository.UserRepository;
import com.LongChau.HealthMateLC.service.UserService;
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

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Tên đăng nhập không tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }

            Pharmacy pharmacy = userService.findPharmacyByUsername(loginRequest.getUsername());

            if (!user.getIsActive()) {
                response.put("success", false);
                response.put("message",
                        "Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên để biết thêm chi tiết.");
                return ResponseEntity.badRequest().body(response);
            }

            if (pharmacy != null && !pharmacy.getIsActive()) {
                response.put("success", false);
                response.put("message", "Nhà thuốc của bạn hiện không hoạt động.");
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getPassword().equals(loginRequest.getPassword())) {
                session.setAttribute("currentUser", user);
                response.put("redirectUrl", redirectConfig.getRedirectUrl(user.getRole()));
                response.put("success", true);
                response.put("message", "Đăng nhập thành công!");
                response.put("userId", user.getUserId());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Tên Đăng Nhập Hoặc Mật Khẩu Không Chính Xác!");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("Login error " + e.getMessage());
            response.put("success", false);
            response.put("message", "Có Lỗi Xảy Ra " + e.getMessage());
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