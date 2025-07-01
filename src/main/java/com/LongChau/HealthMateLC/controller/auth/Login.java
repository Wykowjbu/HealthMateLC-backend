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
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
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
                response.put("redirectUrl", redirectConfig.getRedirectUrl(user.getRole()));
                response.put("success", true);
                response.put("message", "Đăng nhập thành công!");
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
}