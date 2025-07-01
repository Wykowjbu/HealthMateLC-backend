package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.config.RedirectConfig;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController

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
            if (user != null
                    && user.getPassword().equals(loginRequest.getPassword())
                    && user.getUsername().equals(loginRequest.getUsername())) {
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
