package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * Find the pharmacy associated with a user via UserInformation
     */
    public Pharmacy findPharmacyByUsername(String username) {
        User user = findUserByUsername(username);
        if (user == null) return null;
        UserInformation info = user.getUserInformation();
        return (info != null) ? info.getPharmacy() : null;
    }

    public int countUserByRole(String role) {
        return userRepository.findUsersByRole(role).size();
    }

    public List<User> getAllUser(){return userRepository.findAll(); };
}
