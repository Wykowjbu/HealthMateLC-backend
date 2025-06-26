package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.service.CustomerService;
import com.LongChau.HealthMateLC.service.PharmacyService;
import com.LongChau.HealthMateLC.service.UserInformationService;
import com.LongChau.HealthMateLC.service.UserService;
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

        List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
        map.put("listPharmacies", pharmacies);

        Map<String,Object> map1 = new HashMap<>();
        pharmacies.forEach(pharmacy -> {
            List<UserInformationDTO> listUser = userInformationService.getEmployeeAndManagerByPharmacyId(pharmacy.getPharmacyId());
            map1.put(String.valueOf(pharmacy.getPharmacyId()), listUser);
            System.out.println(listUser.toString());
        });
        map.put("listUsersByPharmacy", map1);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/update-account/{userId}")
    public ResponseEntity<UserInformationDTO> updateUserInformation(@RequestBody UserInformationDTO userInformationDTO, @PathVariable Integer userId) {
        UserInformationDTO userInformationDtoResult = userService.updateUserAndUserInformation(userInformationDTO, userId);
        if (userInformationDtoResult != null) {
            return new ResponseEntity<>(userInformationDtoResult, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
