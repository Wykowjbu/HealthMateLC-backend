package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.model.Customer;
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
        if (isPhoneOrEmailExists(customer)) {
            return ResponseEntity.badRequest().body(customer);
        }
        Customer addNewCustomer=customerService.addNewCustomer(customer);
        return ResponseEntity.ok(addNewCustomer);
    }


    @PutMapping("/cap-nhat-khach-hang/{id}")
    public ResponseEntity<Customer> updateCustomerById(@PathVariable Integer id, @RequestBody Customer customer) {
        // Set the customer ID from path variable
        customer.setCustomerId(id);
        if (isPhoneOrEmailExists(customer)) {
            return ResponseEntity.badRequest().body(customer);
        }
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    public boolean isPhoneOrEmailExists(Customer customer) {
        List<Customer> customers = customerService.getAll();
        for (Customer existingCustomer : customers) {
            if (!existingCustomer.getCustomerId().equals(customer.getCustomerId())) {
                if (existingCustomer.getPhone() != null && existingCustomer.getPhone().equals(customer.getPhone())) {
                    return true;
                }
                if (existingCustomer.getEmail() != null && existingCustomer.getEmail().equals(customer.getEmail())) {
                    return true;
                }
            }
        }
        return false;
    }


}

