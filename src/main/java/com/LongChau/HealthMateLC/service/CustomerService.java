package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Customer> getAll() {
        return (List<Customer>) customerRepository.findAll();
    }

    public Customer addNewCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Customer updatedCustomer) {
        return customerRepository.save(updatedCustomer);
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id).orElse(null);
    }
}
