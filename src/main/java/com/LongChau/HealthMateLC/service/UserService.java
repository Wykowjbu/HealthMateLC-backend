    package com.LongChau.HealthMateLC.service;

    import com.LongChau.HealthMateLC.dto.UserDTO;
    import com.LongChau.HealthMateLC.dto.UserInformationDTO;
    import com.LongChau.HealthMateLC.model.Pharmacy;
    import com.LongChau.HealthMateLC.model.User;
    import com.LongChau.HealthMateLC.model.UserInformation;
    import com.LongChau.HealthMateLC.repository.UserInformationRepository;
    import com.LongChau.HealthMateLC.repository.UserRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;

    @Service
    public class UserService {
        private final UserRepository userRepository;
        private final UserInformationRepository userInformationRepository;

        @Autowired
        public UserService(UserRepository userRepository, UserInformationRepository userInformationRepository) {
            this.userRepository = userRepository;
            this.userInformationRepository = userInformationRepository;
        }

        public User findUserByUsername(String username) {
            return userRepository.findUserByUsername(username);
        }

        public int countUserByRole(String role) {
            return userRepository.findUsersByRole(role).size();
        }

        public List<User> getAllUser() {
            return userRepository.findAll();
        }

        public boolean existsByUsername(String username) {
            return userRepository.existsByUsername(username);
        }



        public List<String> getDistinctRoles() {
        // Return predefined roles instead of querying database
        return List.of("admin", "manager", "employee", "customer-service");
        }

        public void createUser(UserDTO userDTO) {
            // Create Users record
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setRole(userDTO.getRole().toLowerCase());
            user.setIsActive(true);
            user.setCreatedDate(LocalDateTime.now());
            userRepository.save(user);
        }

        @Transactional
        public UserInformationDTO updateUserAndUserInformation(UserInformationDTO userInformationDTO, Integer userId) {
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                user.setRole(userInformationDTO.getRole());
                user.setIsActive(userInformationDTO.isActive());
                userRepository.save(user);
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
        public Pharmacy findPharmacyByUsername(String username) {
            User user = findUserByUsername(username);
            if (user == null) return null;
            UserInformation info = user.getUserInformation();
            return (info != null) ? info.getPharmacy() : null;
        }
    }

