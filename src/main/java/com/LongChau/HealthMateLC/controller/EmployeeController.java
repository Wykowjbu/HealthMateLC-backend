package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.repository.CustomerRepository;
import com.LongChau.HealthMateLC.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private CustomerService customerService;


    @GetMapping("/danh-sach-khach-hang")
    public ResponseEntity<List<Customer>> getAllCustomers(){
        List<Customer> customers=customerService.getAll();
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/tao-moi-khach-hang")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
        Customer addNewCustomer=customerService.addNewCustomer(customer);
        return ResponseEntity.ok(addNewCustomer);
    }

    @PatchMapping("edit-customer-info")
    public  ResponseEntity<Customer> editCustomer(@RequestBody Customer customer){
        Customer editCustomer=customerService.updateCustomer(customer);
        return ResponseEntity.ok(editCustomer);
    }

}
