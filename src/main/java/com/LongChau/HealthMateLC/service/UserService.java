package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.UserInformationRepository;
import com.LongChau.HealthMateLC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserInformationRepository userInformationRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserInformationRepository userInformationRepository) {
        this.userRepository = userRepository;
        this.userInformationRepository = userInformationRepository;
    }

    @Transactional
    public UserInformationDTO updateUserAndUserInformation(UserInformationDTO userInformationDTO, Integer userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            user.setRole(userInformationDTO.getRole());
            user.setIsActive(userInformationDTO.isActive());
            userRepository.save(user);

            // Assuming UserInformation is a separate entity that needs to be updated
            // You would need to implement the logic to update UserInformation here
            // For example:
             UserInformation userInfo = userInformationRepository.findById(userId).orElse(null);
             if (userInfo != null) {
                 userInfo.setFullName(userInformationDTO.getFullName());
                 userInfo.setPhone(userInformationDTO.getPhone());
                 userInfo.setEmail(userInformationDTO.getEmail());
                 userInformationRepository.save(userInfo);
             }
            return userInformationDTO; // Return the updated DTO
        }
        return null;
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

    public User findUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void resetPassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
