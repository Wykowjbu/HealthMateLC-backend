    package com.LongChau.HealthMateLC.service;

    import com.LongChau.HealthMateLC.dto.UserDTO;
    import com.LongChau.HealthMateLC.model.User;
    import com.LongChau.HealthMateLC.repository.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
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
    }
