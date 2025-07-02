package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.CustomerMessage;
import com.LongChau.HealthMateLC.repository.CustomerMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerMessageService {
    @Autowired
    private CustomerMessageRepository customerMessageRepository;

    public CustomerMessage saveMessage(CustomerMessage message) {
        return customerMessageRepository.save(message);
    }
}
