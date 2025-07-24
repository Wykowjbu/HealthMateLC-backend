package com.LongChau.HealthMateLC.controller.CustomerService;

import com.LongChau.HealthMateLC.dto.CustomerService.PharmacyDTO;
import com.LongChau.HealthMateLC.dto.CustomerService.FeedbackDTO;
import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.model.CustomerMessage;
import com.LongChau.HealthMateLC.model.Feedback;
import com.LongChau.HealthMateLC.model.Invoice;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.InvoiceRepository;
import com.LongChau.HealthMateLC.repository.UserInformationRepository;
import com.LongChau.HealthMateLC.service.CustomerMessageService;
import com.LongChau.HealthMateLC.service.CustomerService;
import com.LongChau.HealthMateLC.service.EmailService;
import com.LongChau.HealthMateLC.service.FeedbackService;
import com.LongChau.HealthMateLC.service.PharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceControllerTest {

    @Mock
    private PharmacyService pharmacyService;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private UserInformationRepository userInformationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private CustomerMessageService customerMessageService;

    @Mock
    private CustomerService customerService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private CustomerServiceController customerServiceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerServiceController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        session = new MockHttpSession();
    }

    // ===== TEST CASES CHO PHARMACY ENDPOINTS =====

    @Test
    void testGetAllPharmacies_Success() throws Exception {
        // Arrange - Sử dụng PharmacyDTO thay vì Map
        List<PharmacyDTO> mockPharmacies = Arrays.asList(
                createMockPharmacyDTO(1, "Nhà thuốc Long Châu Quận 1", "123 Nguyễn Huệ, Q1", "0123456789"),
                createMockPharmacyDTO(2, "Nhà thuốc Long Châu Quận 3", "456 Võ Văn Tần, Q3", "0987654321")
        );

        when(pharmacyService.getAllPharmaciesCS()).thenReturn(mockPharmacies);

        // Act & Assert
        mockMvc.perform(get("/customer-service/pharmacies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Nhà thuốc Long Châu Quận 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Nhà thuốc Long Châu Quận 3"));

        verify(pharmacyService, times(1)).getAllPharmaciesCS();
    }

    @Test
    void testGetAllPharmacies_EmptyList() throws Exception {
        // Arrange
        when(pharmacyService.getAllPharmaciesCS()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/customer-service/pharmacies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(pharmacyService, times(1)).getAllPharmaciesCS();
    }

    // ===== TEST CASES CHO FEEDBACK ENDPOINTS =====

    @Test
    void testGetFeedbacks_AllFeedbacks() throws Exception {
        // Arrange - Sử dụng FeedbackDTO thay vì Map
        List<FeedbackDTO> mockFeedbacks = Arrays.asList(
                createMockFeedbackDTO(1, 1, 1, 5, "Dịch vụ tuyệt vời!", "Resolved"),
                createMockFeedbackDTO(2, 2, 1, 3, "Chờ đợi lâu", "Pending")
        );

        when(feedbackService.getAllFeedback()).thenReturn(mockFeedbacks);

        // Act & Assert
        mockMvc.perform(get("/customer-service/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].feedbackId").value(1))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[1].feedbackId").value(2))
                .andExpect(jsonPath("$[1].rating").value(3));

        verify(feedbackService, times(1)).getAllFeedback();
    }

    @Test
    void testGetFeedbacks_ByPharmacyId() throws Exception {
        // Arrange
        List<FeedbackDTO> mockFeedbacks = Arrays.asList(
                createMockFeedbackDTO(1, 1, 1, 5, "Dịch vụ tuyệt vời!", "Resolved")
        );

        when(feedbackService.getFeedbackByPharmacy(1)).thenReturn(mockFeedbacks);

        // Act & Assert
        mockMvc.perform(get("/customer-service/reviews")
                        .param("pharmacyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pharmacyId").value(1));

        verify(feedbackService, times(1)).getFeedbackByPharmacy(1);
        verify(feedbackService, never()).getAllFeedback();
        verify(feedbackService, never()).getFeedbackByCustomer(any());
    }

    @Test
    void testGetFeedbacks_ByCustomerId() throws Exception {
        // Arrange
        List<FeedbackDTO> mockFeedbacks = Arrays.asList(
                createMockFeedbackDTO(1, 1, 1, 4, "Hài lòng", "Resolved")
        );

        when(feedbackService.getFeedbackByCustomer(1)).thenReturn(mockFeedbacks);

        // Act & Assert
        mockMvc.perform(get("/customer-service/reviews")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId").value(1));

        verify(feedbackService, times(1)).getFeedbackByCustomer(1);
        verify(feedbackService, never()).getAllFeedback();
        verify(feedbackService, never()).getFeedbackByPharmacy(any());
    }

    @Test
    void testGetFeedbackById_Success() throws Exception {
        // Arrange
        FeedbackDTO mockFeedback = createMockFeedbackDTO(1, 1, 1, 5, "Excellent service!", "Resolved");

        when(feedbackService.getFeedbackById(1)).thenReturn(Optional.of(mockFeedback));

        // Act & Assert
        mockMvc.perform(get("/customer-service/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedbackId").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent service!"));

        verify(feedbackService, times(1)).getFeedbackById(1);
    }

    @Test
    void testGetFeedbackById_NotFound() throws Exception {
        // Arrange
        when(feedbackService.getFeedbackById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/customer-service/reviews/999"))
                .andExpect(status().isNotFound());

        verify(feedbackService, times(1)).getFeedbackById(999);
    }

    @Test
    void testUpdateFeedbackStatus_Success() throws Exception {
        // Arrange
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "Resolved");
        statusUpdate.put("handledByUserId", "1");

        Feedback mockFeedback = createMockFeedback(1, 1, 1, 5, "Great!", "Resolved");
        FeedbackDTO mockFeedbackDTO = createMockFeedbackDTO(1, 1, 1, 5, "Great!", "Resolved");

        when(feedbackService.updateFeedbackStatus(1, "Resolved", 1)).thenReturn(mockFeedback);
        when(feedbackService.toDTO(mockFeedback)).thenReturn(mockFeedbackDTO);

        // Act & Assert
        mockMvc.perform(put("/customer-service/reviews/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedbackId").value(1))
                .andExpect(jsonPath("$.status").value("Resolved"));

        verify(feedbackService, times(1)).updateFeedbackStatus(1, "Resolved", 1);
        verify(feedbackService, times(1)).toDTO(mockFeedback);
    }

    @Test
    void testUpdateFeedbackStatus_InvalidUserId() throws Exception {
        // Arrange
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "Resolved");
        statusUpdate.put("handledByUserId", "invalid");

        // Act & Assert
        mockMvc.perform(put("/customer-service/reviews/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest());

        verify(feedbackService, never()).updateFeedbackStatus(any(), anyString(), any());
    }

    @Test
    void testUpdateFeedbackStatus_MissingStatus() throws Exception {
        // Arrange
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("handledByUserId", "1");

        // Act & Assert
        mockMvc.perform(put("/customer-service/reviews/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest());

        verify(feedbackService, never()).updateFeedbackStatus(any(), anyString(), any());
    }

    @Test
    void testUpdateFeedbackStatus_NotFound() throws Exception {
        // Arrange
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "Resolved");

        when(feedbackService.updateFeedbackStatus(999, "Resolved", null)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/customer-service/reviews/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isNotFound());

        verify(feedbackService, times(1)).updateFeedbackStatus(999, "Resolved", null);
    }

    // ===== TEST CASES CHO USER INFORMATION =====

    @Test
    void testGetCurrentUser_Success() throws Exception {
        // Arrange
        User mockUser = createMockUser(1, "admin", "Admin");
        UserInformation mockUserInfo = createMockUserInformation(1, "Admin User", "admin@longchau.com", "0123456789");

        session.setAttribute("user", mockUser);
        when(userInformationRepository.findById(1)).thenReturn(Optional.of(mockUserInfo));

        // Act & Assert
        mockMvc.perform(get("/customer-service/user/current")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.role").value("Admin"))
                .andExpect(jsonPath("$.username").value("admin"));

        verify(userInformationRepository, times(1)).findById(1);
    }

    @Test
    void testGetCurrentUser_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/customer-service/user/current")
                        .session(session))
                .andExpect(status().isUnauthorized());

        verify(userInformationRepository, never()).findById(any());
    }

    @Test
    void testGetCurrentUser_NoUserInformation() throws Exception {
        // Arrange
        User mockUser = createMockUser(1, "admin", "Admin");
        session.setAttribute("user", mockUser);
        when(userInformationRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/customer-service/user/current")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.fullName").isEmpty())
                .andExpect(jsonPath("$.role").value("Admin"))
                .andExpect(jsonPath("$.username").value("admin"));

        verify(userInformationRepository, times(1)).findById(1);
    }

    // ===== TEST CASES CHO MESSAGING/EMAIL =====

    @Test
    void testGetAllMessages_Success() throws Exception {
        // Arrange
        List<CustomerMessage> mockMessages = Arrays.asList(
                createMockCustomerMessage(1, 1, "email", "Chăm sóc khách hàng"),
                createMockCustomerMessage(2, 1, "sms", "Nhắc nhở uống thuốc")
        );

        when(customerMessageService.getAllMessages()).thenReturn(mockMessages);

        // Act & Assert
        mockMvc.perform(get("/customer-service/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].messageId").value(1))
                .andExpect(jsonPath("$[1].messageId").value(2));

        verify(customerMessageService, times(1)).getAllMessages();
    }

    @Test
    void testSendEmailToCustomer_Success() throws Exception {
        // Arrange
        User mockUser = createMockUser(1, "admin", "Admin");
        session.setAttribute("user", mockUser);

        Map<String, Object> emailRequest = new HashMap<>();
        emailRequest.put("to", "customer@gmail.com");
        emailRequest.put("subject", "Chăm sóc khách hàng");
        emailRequest.put("content", "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!");
        emailRequest.put("type", "email");
        emailRequest.put("channel", "email");
        emailRequest.put("target", "individual");
        emailRequest.put("customerId", 1);

        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(customerMessageService.saveMessage(any(CustomerMessage.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/customer-service/send-email")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent successfully"));

        verify(emailService, times(1)).sendSimpleEmail(eq("customer@gmail.com"), eq("Chăm sóc khách hàng"), anyString());
        verify(customerMessageService, times(1)).saveMessage(any(CustomerMessage.class));
    }

    @Test
    void testSendEmailToCustomer_Unauthorized() throws Exception {
        // Arrange
        Map<String, Object> emailRequest = new HashMap<>();
        emailRequest.put("to", "customer@gmail.com");
        emailRequest.put("subject", "Test");
        emailRequest.put("content", "Test content");

        // Act & Assert
        mockMvc.perform(post("/customer-service/send-email")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized: Please login to send email"));

        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    // ===== TEST CASES CHO STATISTICS =====

    @Test
    void testGetMessagesSentCount_Success() throws Exception {
        // Arrange
        when(customerMessageService.countMessages()).thenReturn(150L);

        // Act & Assert
        mockMvc.perform(get("/customer-service/stats/messages-sent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(150));

        verify(customerMessageService, times(1)).countMessages();
    }

    @Test
    void testGetReviewsPendingCount_Success() throws Exception {
        // Arrange
        when(feedbackService.countPendingReviews()).thenReturn(25L);

        // Act & Assert
        mockMvc.perform(get("/customer-service/stats/reviews-pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(25));

        verify(feedbackService, times(1)).countPendingReviews();
    }

    @Test
    void testGetCustomersServedCount_Success() throws Exception {
        // Arrange
        when(customerService.getNumberOfCustomers()).thenReturn(500);

        // Act & Assert
        mockMvc.perform(get("/customer-service/stats/customers-served"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(500));

        verify(customerService, times(1)).getNumberOfCustomers();
    }

    @Test
    void testGetAverageRating_Success() throws Exception {
        // Arrange
        when(feedbackService.getAverageRating()).thenReturn(4.2);

        // Act & Assert
        mockMvc.perform(get("/customer-service/stats/average-rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.average").value(4.2));

        verify(feedbackService, times(1)).getAverageRating();
    }

    // ===== HELPER METHODS =====

    private PharmacyDTO createMockPharmacyDTO(Integer id, String name, String address, String phone) {
        PharmacyDTO pharmacy = new PharmacyDTO();
        pharmacy.setId(id);
        pharmacy.setName(name);
        pharmacy.setAddress(address);
        pharmacy.setPhone(phone);
        return pharmacy;
    }

    private FeedbackDTO createMockFeedbackDTO(Integer feedbackId, Integer pharmacyId, Integer customerId, Integer rating, String comment, String status) {
        FeedbackDTO feedback = new FeedbackDTO();
        feedback.setId(feedbackId);  // Sửa từ setFeedbackId thành setId
        feedback.setRating(rating);
        feedback.setContent(comment);  // Sửa từ setComment thành setContent
        feedback.setStatus(status);

        // Tạo CustomerDTO nested object
        FeedbackDTO.CustomerDTO customerDTO = new FeedbackDTO.CustomerDTO();
        customerDTO.setId(customerId);
        feedback.setCustomer(customerDTO);

        // Tạo PharmacyDTO object
        PharmacyDTO pharmacyDTO = new PharmacyDTO();
        pharmacyDTO.setId(pharmacyId);
        feedback.setPharmacy(pharmacyDTO);

        return feedback;
    }

    private Feedback createMockFeedback(Integer feedbackId, Integer pharmacyId, Integer customerId, Integer rating, String comment, String status) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(feedbackId);

        // Tạo Customer object thay vì setCustomerId
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        feedback.setCustomer(customer);

        // Tạo Pharmacy object thay vì setPharmacyId
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setPharmacyId(pharmacyId);
        feedback.setPharmacy(pharmacy);

        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setStatus(status);
        return feedback;
    }

    private User createMockUser(Integer userId, String username, String role) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setRole(role);
        user.setIsActive(true);
        return user;
    }

    private UserInformation createMockUserInformation(Integer id, String fullName, String email, String phone) {
        UserInformation userInfo = new UserInformation();
        userInfo.setUserId(id);  // Sửa từ setUserInformationId thành setUserId
        userInfo.setFullName(fullName);
        userInfo.setEmail(email);
        userInfo.setPhone(phone);
        return userInfo;
    }

    private CustomerMessage createMockCustomerMessage(Integer messageId, Integer senderId, String messageType, String messageText) {
        CustomerMessage message = new CustomerMessage();
        message.setMessageId(messageId);
        message.setSenderId(senderId);
        message.setMessageType(messageType);
        message.setMessageText(messageText);
        message.setSentAt(LocalDateTime.now());
        return message;
    }
}
