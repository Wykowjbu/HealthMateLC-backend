package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.config.RedirectConfig;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoginTest {

    @Mock
    private UserService userService;

    @Mock
    private RedirectConfig redirectConfig;

    @InjectMocks
    private Login loginController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        objectMapper = new ObjectMapper();
        session = new MockHttpSession();
    }

    // ===== TEST CASES CHO GET ALL USERS =====

    @Test
    void testGetAllUsers_Success() throws Exception {
        // Arrange
        List<User> mockUsers = Arrays.asList(
                createMockUser(1, "admin", "password123", "Admin", true),
                createMockUser(2, "employee1", "password123", "Employee", true)
        );

        when(userService.getAllUser()).thenReturn(mockUsers);

        // Act & Assert
        mockMvc.perform(get("/api/auth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].username").value("employee1"));

        verify(userService, times(1)).getAllUser();
    }

    @Test
    void testGetAllUsers_EmptyList() throws Exception {
        // Arrange
        when(userService.getAllUser()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/auth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAllUser();
    }

    // ===== TEST CASES CHO LOGIN =====

    @Test
    void testLogin_Success_Admin() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "password123");

        User mockUser = createMockUser(1, "admin", "password123", "Admin", true);
        Pharmacy mockPharmacy = createMockPharmacy(1, "Nhà thuốc Long Châu", true);

        when(userService.findUserByUsername("admin")).thenReturn(mockUser);
        when(userService.findPharmacyByUsername("admin")).thenReturn(mockPharmacy);
        when(redirectConfig.getRedirectUrl("Admin")).thenReturn("/admin/dashboard");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng nhập thành công!"))
                .andExpect(jsonPath("$.redirectUrl").value("/admin/dashboard"));

        verify(userService, times(1)).findUserByUsername("admin");
        verify(userService, times(1)).findPharmacyByUsername("admin");
        verify(redirectConfig, times(1)).getRedirectUrl("Admin");
    }

    @Test
    void testLogin_Success_Employee() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "employee1");
        loginRequest.put("password", "emp123");

        User mockUser = createMockUser(2, "employee1", "emp123", "Employee", true);
        Pharmacy mockPharmacy = createMockPharmacy(1, "Nhà thuốc Long Châu", true);

        when(userService.findUserByUsername("employee1")).thenReturn(mockUser);
        when(userService.findPharmacyByUsername("employee1")).thenReturn(mockPharmacy);
        when(redirectConfig.getRedirectUrl("Employee")).thenReturn("/employee/dashboard");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng nhập thành công!"))
                .andExpect(jsonPath("$.redirectUrl").value("/employee/dashboard"));

        verify(userService, times(1)).findUserByUsername("employee1");
        verify(userService, times(1)).findPharmacyByUsername("employee1");
        verify(redirectConfig, times(1)).getRedirectUrl("Employee");
    }

    @Test
    void testLogin_Success_WithoutPharmacy() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "password123");

        User mockUser = createMockUser(1, "admin", "password123", "Admin", true);

        when(userService.findUserByUsername("admin")).thenReturn(mockUser);
        when(userService.findPharmacyByUsername("admin")).thenReturn(null); // No pharmacy
        when(redirectConfig.getRedirectUrl("Admin")).thenReturn("/admin/dashboard");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng nhập thành công!"))
                .andExpect(jsonPath("$.redirectUrl").value("/admin/dashboard"));

        verify(userService, times(1)).findUserByUsername("admin");
        verify(userService, times(1)).findPharmacyByUsername("admin");
        verify(redirectConfig, times(1)).getRedirectUrl("Admin");
    }

    @Test
    void testLogin_Failed_UserInactive() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "inactive_user");
        loginRequest.put("password", "password123");

        User mockUser = createMockUser(3, "inactive_user", "password123", "Employee", false); // Inactive user

        when(userService.findUserByUsername("inactive_user")).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên để biết thêm chi tiết."));

        verify(userService, times(1)).findUserByUsername("inactive_user");
        verify(userService, never()).findPharmacyByUsername(any());
        verify(redirectConfig, never()).getRedirectUrl(any());
    }

    @Test
    void testLogin_Failed_PharmacyInactive() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "employee1");
        loginRequest.put("password", "emp123");

        User mockUser = createMockUser(2, "employee1", "emp123", "Employee", true);
        Pharmacy mockPharmacy = createMockPharmacy(1, "Nhà thuốc bị khóa", false); // Inactive pharmacy

        when(userService.findUserByUsername("employee1")).thenReturn(mockUser);
        when(userService.findPharmacyByUsername("employee1")).thenReturn(mockPharmacy);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Nhà thuốc của bạn hiện không hoạt động."));

        verify(userService, times(1)).findUserByUsername("employee1");
        verify(userService, times(1)).findPharmacyByUsername("employee1");
        verify(redirectConfig, never()).getRedirectUrl(any());
    }

    @Test
    void testLogin_Failed_WrongPassword() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "wrongpassword");

        User mockUser = createMockUser(1, "admin", "password123", "Admin", true);
        Pharmacy mockPharmacy = createMockPharmacy(1, "Nhà thuốc Long Châu", true);

        when(userService.findUserByUsername("admin")).thenReturn(mockUser);
        when(userService.findPharmacyByUsername("admin")).thenReturn(mockPharmacy);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Tên đăng nhập hoặc mật khẩu không chính xác!"));

        verify(userService, times(1)).findUserByUsername("admin");
        verify(userService, times(1)).findPharmacyByUsername("admin");
        verify(redirectConfig, never()).getRedirectUrl(any());
    }

    @Test
    void testLogin_Failed_UserNotFound() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "nonexistent");
        loginRequest.put("password", "password123");

        when(userService.findUserByUsername("nonexistent")).thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Có lỗi xảy ra: User not found")));

        verify(userService, times(1)).findUserByUsername("nonexistent");
        verify(userService, never()).findPharmacyByUsername(any());
        verify(redirectConfig, never()).getRedirectUrl(any());
    }

    @Test
    void testLogin_Failed_ServiceException() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "password123");

        when(userService.findUserByUsername("admin")).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Có lỗi xảy ra: Database connection error")));

        verify(userService, times(1)).findUserByUsername("admin");
        verify(userService, never()).findPharmacyByUsername(any());
        verify(redirectConfig, never()).getRedirectUrl(any());
    }

    @Test
    void testLogin_EmptyUsername() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "");
        loginRequest.put("password", "password123");

        when(userService.findUserByUsername("")).thenThrow(new RuntimeException("Username is required"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Có lỗi xảy ra:")));

        verify(userService, times(1)).findUserByUsername("");
    }

    @Test
    void testLogin_EmptyPassword() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "");

        User mockUser = createMockUser(1, "admin", "password123", "Admin", true);
        Pharmacy mockPharmacy = createMockPharmacy(1, "Nhà thuốc Long Châu", true);

        when(userService.findUserByUsername("admin")).thenReturn(mockUser);
        when(userService.findPharmacyByUsername("admin")).thenReturn(mockPharmacy);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Tên đăng nhập hoặc mật khẩu không chính xác!"));

        verify(userService, times(1)).findUserByUsername("admin");
        verify(userService, times(1)).findPharmacyByUsername("admin");
    }

    // ===== HELPER METHODS =====

    private User createMockUser(Integer userId, String username, String password, String role, Boolean isActive) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setIsActive(isActive);
        return user;
    }

    private Pharmacy createMockPharmacy(Integer pharmacyId, String pharmacyName, Boolean isActive) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setPharmacyId(pharmacyId);
        pharmacy.setPharmacyName(pharmacyName);
        pharmacy.setIsActive(isActive);
        return pharmacy;
    }
}
