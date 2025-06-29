package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.dto.UserDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.service.CustomerService;
import com.LongChau.HealthMateLC.service.PharmacyService;
import com.LongChau.HealthMateLC.service.UserInformationService;
import com.LongChau.HealthMateLC.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "${app.frontend.base-url}")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PharmacyService pharmacyService;
    @Autowired
    private UserInformationService userInformationService;

    @GetMapping("/list-accounts")
    public ResponseEntity<Map<String, Object>> listAccounts() {
        Map<String, Object> map = new HashMap<>();
        List<Integer> numberList = new ArrayList<>();
        numberList.add(userService.countUserByRole("employee"));
        numberList.add(pharmacyService.getNumberOfPharmacies());
        numberList.add(customerService.getNumberOfCustomers());
        map.put("listNumbers", numberList);

        List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
        map.put("listPharmacies", pharmacies);

        Map<String, Object> map1 = new HashMap<>();
        pharmacies.forEach(pharmacy -> {
            List<UserInformationDTO> listUser = userInformationService.getEmployeeAndManagerByPharmacyId(pharmacy.getPharmacyId());
            map1.put(String.valueOf(pharmacy.getPharmacyId()), listUser);
        });
        map.put("listUsersByPharmacy", map1);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/list-pharmacies")
    public ResponseEntity<List<Pharmacy>> listPharmacies() {
        List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
        return new ResponseEntity<>(pharmacies, HttpStatus.OK);
    }

    @GetMapping("/list-roles")
    public ResponseEntity<List<String>> listRoles() {
        List<String> roles = userService.getDistinctRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping("/add-account")
    public ResponseEntity<Map<String, String>> addAccount(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();

        // Validate input
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            response.put("message", "Tên đăng nhập không được để trống");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (userService.existsByUsername(userDTO.getUsername())) {
            response.put("message", "Tên đăng nhập đã tồn tại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            response.put("message", "Mật khẩu không được để trống");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (userDTO.getPassword().length() < 6) {
            response.put("message", "Mật khẩu phải có ít nhất 6 ký tự");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (userDTO.getFullName() == null || userDTO.getFullName().trim().isEmpty()) {
            response.put("message", "Họ và tên không được để trống");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            if (!userDTO.getEmail().matches(emailRegex)) {
                response.put("message", "Email không đúng định dạng");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            String phoneRegex = "^0\\d{9}$";
            if (!userDTO.getPhone().matches(phoneRegex)) {
                response.put("message", "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        if (userDTO.getRole() == null || userDTO.getRole().trim().isEmpty()) {
            response.put("message", "Vai trò không được để trống");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!userService.getDistinctRoles().contains(userDTO.getRole().toLowerCase())) {
            response.put("message", "Vai trò không hợp lệ");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (userDTO.getPharmacyId() != null && !pharmacyService.existsById(userDTO.getPharmacyId())) {
            response.put("message", "Nhà thuốc không tồn tại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Create user
            UserDTO userToSave = new UserDTO(
                    userDTO.getUsername(),
                    userDTO.getPassword(), // Store password as plain text
                    userDTO.getFullName(),
                    userDTO.getPhone(),
                    userDTO.getEmail(),
                    userDTO.getRole().toLowerCase(),
                    userDTO.getPharmacyId()
            );
            userService.createUser(userToSave);

            // Create user information
            User createdUser = userService.findUserByUsername(userDTO.getUsername());
            if (createdUser != null) {
                UserInformation userInformation = new UserInformation();
                userInformation.setUser(createdUser); // @MapsId sẽ tự động set userId
                userInformation.setFullName(userDTO.getFullName());
                userInformation.setPhone(userDTO.getPhone());
                userInformation.setEmail(userDTO.getEmail());
                
                // Set pharmacy if provided
                if (userDTO.getPharmacyId() != null) {
                    Optional<Pharmacy> pharmacy = pharmacyService.findById(userDTO.getPharmacyId());
                    if (pharmacy.isPresent()) {
                        userInformation.setPharmacy(pharmacy.get());
                    }
                }
                
                userInformationService.save(userInformation);
            }

            response.put("message", "Tạo tài khoản thành công");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Lỗi khi tạo tài khoản: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}