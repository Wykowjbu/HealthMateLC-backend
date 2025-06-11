package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    private UserRepository userRepository;

    @GetMapping
    private List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User loginRequest){
        Map<String,Object> responce = new HashMap<>();

        try {
            User user=userRepository.findByUsername(loginRequest.getUsername());
            if (user !=null
                    && user.getPassword().equals(loginRequest.getPassword())
                    && user.getUsername().equals(loginRequest.getUsername())){
                responce.put("success",true);
                responce.put("message","Đăng nhập thành công!");
                responce.put("redirectUrl","http://127.0.0.1:5500/welcome/welcome.html");

                return ResponseEntity.ok(responce);
            } else {
                responce.put("success",false);
            responce.put("message","Tên Đăng Nhập Hoặc Mật Khẩu Không Chính Xác! ");
                return ResponseEntity.badRequest().body(responce);
            }
        } catch (Exception e) {
            System.err.println("Login error "+e.getMessage());
            responce.put("success",false);
            responce.put("message","Có Lỗi Xảy Ra "+e.getMessage());
            return  ResponseEntity.internalServerError().body(responce);
        }
    }
}
