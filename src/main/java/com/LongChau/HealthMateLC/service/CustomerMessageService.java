package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.CustomerMessage;
import com.LongChau.HealthMateLC.repository.CustomerMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerMessageService {
    @Autowired
    private CustomerMessageRepository customerMessageRepository;

    public CustomerMessage saveMessage(CustomerMessage message) {
        return customerMessageRepository.save(message);
    }

    // Đếm tổng số tin nhắn đã gửi
    public long countMessages() {
        return customerMessageRepository.count();
    }

    // Lấy tất cả tin nhắn (nếu cần cho bảng)
    public List<CustomerMessage> getAllMessages() {
        return customerMessageRepository.findAll();
    }
}
