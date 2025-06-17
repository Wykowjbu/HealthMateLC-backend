package com.LongChau.HealthMateLC;

import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class HealthMateLcApplication {
    @Autowired
    private CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication.run(HealthMateLcApplication.class, args);
    }

    @PostConstruct
    public void printAllCustomers() {
        List<Customer> list = customerRepository.findAll();
        list.forEach(System.out::println);
    }

    // Nếu bạn vẫn muốn giữ phương thức getList()
    public List<Customer> getList() {
        return customerRepository.findAll();
    }
}
