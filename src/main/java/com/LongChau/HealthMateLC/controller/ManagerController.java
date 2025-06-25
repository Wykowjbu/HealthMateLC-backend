package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.UserInformationRepository;
import com.LongChau.HealthMateLC.repository.UserRepository;
import com.LongChau.HealthMateLC.service.UserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ManagerController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInformationService userInformationService;

    @GetMapping("/manager/profile")
    public ResponseEntity<?> getManagerProfile(
            HttpSession session,
            @RequestParam(value = "detail", required = false, defaultValue = "false") boolean detail) {

        System.out.println("DEBUG: Request to /manager/profile");
        System.out.println("DEBUG: Session ID from request: " + session.getId());
        System.out.println("DEBUG: Session is new: " + session.isNew());
        System.out.println("DEBUG: All session attributes: " + Collections.list(session.getAttributeNames()));

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            System.out.println("DEBUG: currentUser is NULL in session.");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }

        System.out.println("DEBUG: currentUser found: " + currentUser.getUsername() + ", Role: " + currentUser.getRole());

        if (!"MANAGER".equals(currentUser.getRole().toUpperCase())) {
            System.out.println("DEBUG: Invalid role: " + currentUser.getRole());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy cập");
            return ResponseEntity.status(403).body(errorResponse);
        }

        User manager = userRepository.findById(currentUser.getUserId()).orElse(null);
        if (manager == null) {
            System.out.println("DEBUG: Manager not found in DB for userId: " + currentUser.getUserId());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "Không tìm thấy thông tin người dùng");
            return ResponseEntity.status(404).body(errorResponse);
        }

        System.out.println("DEBUG: Manager found in DB: " + manager.getUsername());
        UserInformation userInfo = userInformationService.findUserInformationByUserId(manager.getUserId());

        Map<String, Object> profile = new HashMap<>();

        if (userInfo != null) {
            profile.put("fullName", userInfo.getFullName());
            if (userInfo.getPharmacy() != null) {
                profile.put("pharmacyName", userInfo.getPharmacy().getPharmacyName());
            } else {
                profile.put("pharmacyName", "Chưa gán chi nhánh");
            }
        } else {
            profile.put("fullName", manager.getUsername());
            profile.put("pharmacyName", "Chưa gán chi nhánh");
        }

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/manager/showprofile")
    public ResponseEntity<?> showManagerProfile(HttpSession session) {
        System.out.println("DEBUG: Request to /manager/showprofile");
        System.out.println("DEBUG: Session ID from request: " + session.getId());
        System.out.println("DEBUG: Session is new: " + session.isNew());
        System.out.println("DEBUG: All session attributes: " + Collections.list(session.getAttributeNames()));

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            System.out.println("DEBUG: currentUser is NULL in session.");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }

        System.out.println("DEBUG: currentUser found: " + currentUser.getUsername() + ", Role: " + currentUser.getRole());

        if (!"MANAGER".equals(currentUser.getRole().toUpperCase())) {
            System.out.println("DEBUG: Invalid role: " + currentUser.getRole());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy cập");
            return ResponseEntity.status(403).body(errorResponse);
        }

        User manager = userRepository.findById(currentUser.getUserId()).orElse(null);
        if (manager == null) {
            System.out.println("DEBUG: Manager not found in DB for userId: " + currentUser.getUserId());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "Không tìm thấy thông tin người dùng");
            return ResponseEntity.status(404).body(errorResponse);
        }

        System.out.println("DEBUG: Manager found in DB: " + manager.getUsername());
        UserInformation userInfo = userInformationService.findUserInformationByUserId(manager.getUserId());

        Map<String, Object> profile = new HashMap<>();

        if (userInfo != null) {
            profile.put("fullName", userInfo.getFullName());
            profile.put("phone", userInfo.getPhone() != null ? userInfo.getPhone() : "Chưa cập nhật");
            profile.put("email", userInfo.getEmail() != null ? userInfo.getEmail() : "Chưa cập nhật");
            if (userInfo.getPharmacy() != null) {
                Pharmacy pharmacy = userInfo.getPharmacy();
                profile.put("pharmacyName", pharmacy.getPharmacyName());
                profile.put("pharmacyId", pharmacy.getPharmacyId());
                profile.put("pharmacyAddress", pharmacy.getAddress() != null ? pharmacy.getAddress() : "Chưa cập nhật");
                profile.put("pharmacyPhone", pharmacy.getPhone() != null ? pharmacy.getPhone() : "Chưa cập nhật");
            } else {
                profile.put("pharmacyName", "Chưa gán chi nhánh");
                profile.put("pharmacyId", null);
                profile.put("pharmacyAddress", "Chưa gán chi nhánh");
                profile.put("pharmacyPhone", "Chưa gán chi nhánh");
            }
        } else {
            profile.put("fullName", manager.getUsername());
            profile.put("phone", "Chưa cập nhật");
            profile.put("email", "Chưa cập nhật");
            profile.put("pharmacyName", "Chưa gán chi nhánh");
            profile.put("pharmacyId", null);
            profile.put("pharmacyAddress", "Chưa gán chi nhánh");
            profile.put("pharmacyPhone", "Chưa gán chi nhánh");
        }

        System.out.println("DEBUG: Returning full profile: " + profile);
        return ResponseEntity.ok(profile);
    }
}