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
    public ResponseEntity<Map<String, Object>> login(@RequestBody User loginRequest, HttpSession session) {
        Map<String, Object> responce = new HashMap<>();
        try {
            User user = userService.findUserByUsername(loginRequest.getUsername());
            Pharmacy pharmacy = userService.findPharmacyByUsername(loginRequest.getUsername()); ;
            if (!user.getIsActive()){
                responce.put("success", false);
                responce.put("message", "Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên để biết thêm chi tiết.");
                return ResponseEntity.badRequest().body(responce);
            }

            // Check pharmacy active status (null pharmacy allowed)
            if (pharmacy != null && !pharmacy.getIsActive()) {
                responce.put("success", false);
                responce.put("message", "Nhà thuốc của bạn hiện không hoạt động.");
                return ResponseEntity.badRequest().body(responce);
            }
            else if (user != null
                    && user.getPassword().equals(loginRequest.getPassword())
                    && user.getUsername().equals(loginRequest.getUsername())) {

                // Lưu user vào session
                session.setAttribute("user", user);

                responce.put("redirectUrl", redirectConfig.getRedirectUrl(user.getRole()));
                responce.put("success", true);
                responce.put("message", "Đăng nhập thành công!");

                return ResponseEntity.ok(responce);
            } else {
                responce.put("success", false);
                responce.put("message", "Tên Đăng Nhập Hoặc Mật Khẩu Không Chính Xác! ");
                return ResponseEntity.badRequest().body(responce);
            }
        } catch (Exception e) {
            System.err.println("Login error " + e.getMessage());
            responce.put("success", false);
            responce.put("message", "Có Lỗi Xảy Ra " + e.getMessage());
            return ResponseEntity.internalServerError().body(responce);
        }
    }
}