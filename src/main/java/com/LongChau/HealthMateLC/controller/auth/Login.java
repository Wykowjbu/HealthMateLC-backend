package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.config.RedirectConfig;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class Login {
    @Autowired
    private RedirectConfig redirectConfig;

    @Autowired
    private UserService userService;

    @GetMapping
    private List<User> getAllUsers() {
        return userService.getAllUser();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.findUserByUsername(loginRequest.getUsername());
            Pharmacy pharmacy = userService.findPharmacyByUsername(loginRequest.getUsername()); ;
            if (!user.getIsActive()){
                response.put("success", false);
                response.put("message", "Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên để biết thêm chi tiết.");
                return ResponseEntity.badRequest().body(responce);
            }

            // Check pharmacy active status (null pharmacy allowed)
            if (pharmacy != null && !pharmacy.getIsActive()) {
                response.put("success", false);
                response.put("message", "Nhà thuốc của bạn hiện không hoạt động.");
                return ResponseEntity.badRequest().body(responce);
            }
            else if (user != null
                    && user.getPassword().equals(loginRequest.getPassword())
                    && user.getUsername().equals(loginRequest.getUsername())) {

                // Lưu user vào session
                session.setAttribute("user", user);

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