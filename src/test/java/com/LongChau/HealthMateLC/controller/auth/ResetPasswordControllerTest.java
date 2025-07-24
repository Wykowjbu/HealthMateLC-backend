package com.LongChau.HealthMateLC.controller.auth;

import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.service.EmailService;
import com.LongChau.HealthMateLC.service.OtpStorage;
import com.LongChau.HealthMateLC.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordControllerTest {

    @Mock
    private OtpStorage otpStorage;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private ResetPasswordController resetPasswordController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resetPasswordController).build();
    }

    // ===== TEST CASES CHO REQUEST OTP =====

    @Test
    void testRequestOtp_Success() throws Exception {
        // Arrange
        String username = "testuser";
        String email = "testuser@gmail.com";

        User mockUser = createMockUser(1, username, "password123");
        UserInformation mockUserInfo = createMockUserInformation(1, "Test User", email);
        mockUser.setUserInformation(mockUserInfo);

        when(passwordService.findByUsername(username)).thenReturn(Optional.of(mockUser));
        doNothing().when(otpStorage).storeOtp(eq(username), anyString());
        doNothing().when(emailService).sendOtpEmail(eq(email), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/auth/request-otp")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP đã gửi"));

        verify(passwordService, times(1)).findByUsername(username);
        verify(otpStorage, times(1)).storeOtp(eq(username), anyString());
        verify(emailService, times(1)).sendOtpEmail(eq(email), anyString());
    }

    @Test
    void testRequestOtp_UserNotFound() throws Exception {
        // Arrange
        String username = "nonexistent";

        when(passwordService.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/auth/request-otp")
                        .param("username", username))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username không tồn tại"));

        verify(passwordService, times(1)).findByUsername(username);
        verify(otpStorage, never()).storeOtp(anyString(), anyString());
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    @Test
    void testRequestOtp_UserInformationNull() throws Exception {
        // Arrange
        String username = "userwithoutemail";

        User mockUser = createMockUser(1, username, "password123");
        mockUser.setUserInformation(null); // No user information

        when(passwordService.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(post("/api/auth/request-otp")
                        .param("username", username))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email không tồn tại"));

        verify(passwordService, times(1)).findByUsername(username);
        verify(otpStorage, never()).storeOtp(anyString(), anyString());
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    @Test
    void testRequestOtp_EmailServiceException() throws Exception {
        // Arrange
        String username = "testuser";
        String email = "testuser@gmail.com";

        User mockUser = createMockUser(1, username, "password123");
        UserInformation mockUserInfo = createMockUserInformation(1, "Test User", email);
        mockUser.setUserInformation(mockUserInfo);

        when(passwordService.findByUsername(username)).thenReturn(Optional.of(mockUser));
        doNothing().when(otpStorage).storeOtp(eq(username), anyString());
        doThrow(new RuntimeException("Email service unavailable"))
                .when(emailService).sendOtpEmail(eq(email), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/auth/request-otp")
                        .param("username", username))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Lỗi gửi OTP: Email service unavailable")));

        verify(passwordService, times(1)).findByUsername(username);
        verify(otpStorage, times(1)).storeOtp(eq(username), anyString());
        verify(emailService, times(1)).sendOtpEmail(eq(email), anyString());
    }

    @Test
    void testRequestOtp_EmptyUsername() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/request-otp")
                        .param("username", ""))
                .andExpect(status().isBadRequest());

        verify(passwordService, times(1)).findByUsername("");
        verify(otpStorage, never()).storeOtp(anyString(), anyString());
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    // ===== TEST CASES CHO VERIFY OTP =====

    @Test
    void testVerifyOtp_Success() throws Exception {
        // Arrange
        String username = "testuser";
        String otp = "123456";

        when(otpStorage.verifyOtp(username, otp)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/verify-otp")
                        .param("username", username)
                        .param("otp", otp))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP hợp lệ"));

        verify(otpStorage, times(1)).verifyOtp(username, otp);
    }

    @Test
    void testVerifyOtp_InvalidOtp() throws Exception {
        // Arrange
        String username = "testuser";
        String otp = "wrong123";

        when(otpStorage.verifyOtp(username, otp)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/auth/verify-otp")
                        .param("username", username)
                        .param("otp", otp))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("OTP không hợp lệ"));

        verify(otpStorage, times(1)).verifyOtp(username, otp);
    }

    @Test
    void testVerifyOtp_ExpiredOtp() throws Exception {
        // Arrange
        String username = "testuser";
        String otp = "123456";

        when(otpStorage.verifyOtp(username, otp)).thenReturn(false); // OTP expired or invalid

        // Act & Assert
        mockMvc.perform(post("/api/auth/verify-otp")
                        .param("username", username)
                        .param("otp", otp))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("OTP không hợp lệ"));

        verify(otpStorage, times(1)).verifyOtp(username, otp);
    }

    @Test
    void testVerifyOtp_EmptyParameters() throws Exception {
        // Act & Assert - Empty username
        mockMvc.perform(post("/api/auth/verify-otp")
                        .param("username", "")
                        .param("otp", "123456"))
                .andExpect(status().isBadRequest());

        // Act & Assert - Empty OTP
        mockMvc.perform(post("/api/auth/verify-otp")
                        .param("username", "testuser")
                        .param("otp", ""))
                .andExpect(status().isBadRequest());
    }

    // ===== TEST CASES CHO CHANGE PASSWORD =====

    @Test
    void testChangePassword_Success() throws Exception {
        // Arrange
        String username = "testuser";
        String newPassword = "newpassword123";

        User mockUser = createMockUser(1, username, "oldpassword");

        when(passwordService.findByUsername(username)).thenReturn(Optional.of(mockUser));
        doNothing().when(passwordService).changePassword(username, newPassword);

        // Act & Assert
        mockMvc.perform(post("/api/auth/change-password")
                        .param("username", username)
                        .param("newPassword", newPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("Đổi mật khẩu thành công"));

        verify(passwordService, times(1)).findByUsername(username);
        verify(passwordService, times(1)).changePassword(username, newPassword);
    }

    @Test
    void testChangePassword_UserNotFound() throws Exception {
        // Arrange
        String username = "nonexistent";
        String newPassword = "newpassword123";

        when(passwordService.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/auth/change-password")
                        .param("username", username)
                        .param("newPassword", newPassword))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username không tồn tại"));

        verify(passwordService, times(1)).findByUsername(username);
        verify(passwordService, never()).changePassword(anyString(), anyString());
    }

    @Test
    void testChangePassword_EmptyPassword() throws Exception {
        // Arrange
        String username = "testuser";

        // Act & Assert - Null password
        mockMvc.perform(post("/api/auth/change-password")
                        .param("username", username)
                        .param("newPassword", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mật khẩu không hợp lệ"));

        verify(passwordService, never()).findByUsername(anyString());
        verify(passwordService, never()).changePassword(anyString(), anyString());
    }

    @Test
    void testChangePassword_ServiceException() throws Exception {
        // Arrange
        String username = "testuser";
        String newPassword = "newpassword123";

        User mockUser = createMockUser(1, username, "oldpassword");

        when(passwordService.findByUsername(username)).thenReturn(Optional.of(mockUser));
        doThrow(new RuntimeException("Database error")).when(passwordService).changePassword(username, newPassword);

        // Act & Assert
        mockMvc.perform(post("/api/auth/change-password")
                        .param("username", username)
                        .param("newPassword", newPassword))
                .andExpect(status().isInternalServerError());

        verify(passwordService, times(1)).findByUsername(username);
        verify(passwordService, times(1)).changePassword(username, newPassword);
    }

    @Test
    void testChangePassword_WeakPassword() throws Exception {
        // Arrange
        String username = "testuser";
        String weakPassword = "123"; // Very weak password

        User mockUser = createMockUser(1, username, "oldpassword");

        when(passwordService.findByUsername(username)).thenReturn(Optional.of(mockUser));
        doNothing().when(passwordService).changePassword(username, weakPassword);

        // Act & Assert - Should still work as no validation implemented
        mockMvc.perform(post("/api/auth/change-password")
                        .param("username", username)
                        .param("newPassword", weakPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("Đổi mật khẩu thành công"));

        verify(passwordService, times(1)).findByUsername(username);
        verify(passwordService, times(1)).changePassword(username, weakPassword);
    }

    // ===== INTEGRATION TEST SCENARIOS =====

    @Test
    void testCompletePasswordResetFlow_Success() throws Exception {
        // Arrange
        String username = "testuser";
        String email = "testuser@gmail.com";
        String otp = "123456";
        String newPassword = "newpassword123";

        User mockUser = createMockUser(1, username, "oldpassword");
        UserInformation mockUserInfo = createMockUserInformation(1, "Test User", email);
        mockUser.setUserInformation(mockUserInfo);

        // Step 1: Request OTP
        when(passwordService.findByUsername(username)).thenReturn(Optional.of(mockUser));
        doNothing().when(otpStorage).storeOtp(eq(username), anyString());
        doNothing().when(emailService).sendOtpEmail(eq(email), anyString());

        mockMvc.perform(post("/api/auth/request-otp")
                        .param("username", username))
                .andExpect(status().isOk());

        // Step 2: Verify OTP
        when(otpStorage.verifyOtp(username, otp)).thenReturn(true);

        mockMvc.perform(post("/api/auth/verify-otp")
                        .param("username", username)
                        .param("otp", otp))
                .andExpect(status().isOk());

        // Step 3: Change Password
        doNothing().when(passwordService).changePassword(username, newPassword);

        mockMvc.perform(post("/api/auth/change-password")
                        .param("username", username)
                        .param("newPassword", newPassword))
                .andExpect(status().isOk());

        // Verify all services were called
        verify(passwordService, times(2)).findByUsername(username); // Called in step 1 and 3
        verify(otpStorage, times(1)).storeOtp(eq(username), anyString());
        verify(emailService, times(1)).sendOtpEmail(eq(email), anyString());
        verify(otpStorage, times(1)).verifyOtp(username, otp);
        verify(passwordService, times(1)).changePassword(username, newPassword);
    }

    // ===== HELPER METHODS =====

    private User createMockUser(Integer userId, String username, String password) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    private UserInformation createMockUserInformation(Integer id, String fullName, String email) {
        UserInformation userInfo = new UserInformation();
        userInfo.setUserId(id);
        userInfo.setFullName(fullName);
        userInfo.setEmail(email);
        return userInfo;
    }
}
