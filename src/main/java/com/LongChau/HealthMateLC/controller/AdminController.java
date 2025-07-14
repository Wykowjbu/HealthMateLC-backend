package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.dto.PharmacyDTO;
import com.LongChau.HealthMateLC.dto.ResetPasswordRequest;
import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.service.CustomerService;
import com.LongChau.HealthMateLC.service.PharmacyService;
import com.LongChau.HealthMateLC.service.UserInformationService;
import com.LongChau.HealthMateLC.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String,Object>> listAccounts() {
        Map<String,Object> map = new HashMap<>();
        List<Integer> numberList = new ArrayList<>();
        numberList.add(userService.countUserByRole("employee"));
        numberList.add(pharmacyService.getNumberOfPharmacies());
        numberList.add(customerService.getNumberOfCustomers());
        map.put("listNumbers", numberList);

        List<PharmacyDTO> pharmacies = pharmacyService.getAllPharmaciesDTO();
        map.put("listPharmacies", pharmacies);

        Map<String,Object> map1 = new HashMap<>();
        pharmacies.forEach(pharmacy -> {
            List<UserInformationDTO> listUser = userInformationService.getEmployeeAndManagerByPharmacyId(pharmacy.getPharmacyId());
            map1.put(String.valueOf(pharmacy.getPharmacyId()), listUser);
        });
        map.put("listUsersByPharmacy", map1);
        return ResponseEntity.ok().body(map);
    }

    @PutMapping("/update-account/{userId}")
    public ResponseEntity<UserInformationDTO> updateUserInformation(@RequestBody UserInformationDTO userInformationDTO, @PathVariable Integer userId) {
        UserInformationDTO userInformationDtoResult = userService.updateUserAndUserInformation(userInformationDTO, userId);
        if (userInformationDtoResult != null) {
            return new ResponseEntity<>(userInformationDtoResult, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/reset-password/{userId}")
    public ResponseEntity<?> resetPassword(@PathVariable Integer userId, @RequestBody ResetPasswordRequest resetPasswordRequest) {
        Map<String, String> response = new HashMap<>();
        try{
            System.out.println("Resetting password for user ID: " + userId + " with new password: " + resetPasswordRequest.getNewPassword());
            userService.resetPassword(userId, resetPasswordRequest.getNewPassword());
            response.put("message", "Password reset successfully");
            return new ResponseEntity<>( response ,HttpStatus.OK);
        }catch (Exception e){
            response.put("mesage","Đặt lại mật khẩu không thành công");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
