package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public int getNumberOfCustomers() {
        return (int) customerRepository.count();
    }
}
