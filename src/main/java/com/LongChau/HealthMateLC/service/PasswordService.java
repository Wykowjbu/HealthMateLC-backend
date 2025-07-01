package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordService {
    private final UserRepository userRepository;

    public PasswordService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean changePassword(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setPassword(newPassword); // Lưu plain text tạm thời
        userRepository.save(user);
        return true;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String getEmailByUsername(String username) {
        Optional<User> userOpt = findByUsername(username);
        return userOpt.filter(user -> user.getUserInformation() != null)
                .map(user -> user.getUserInformation().getEmail())
                .orElse(null);
    }
}
