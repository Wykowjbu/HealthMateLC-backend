package com.LongChau.HealthMateLC.controller.customer;

import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ListCustomer {
    @Autowired
    private CustomerRepository customerRepository;


    @PostMapping("/danh-sach-khach-hang")
    public ResponseEntity<List<Customer>> getAllCustomer(){
        List<Customer> customerList=customerRepository.findAll();
        return ResponseEntity.ok(customerList);
    }
}
