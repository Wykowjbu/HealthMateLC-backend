package com.LongChau.HealthMateLC.controller.CustomerService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LongChau.HealthMateLC.dto.CustomerService.UserInformationCsDTO;
import com.LongChau.HealthMateLC.dto.CustomerService.FeedbackDTO;
import com.LongChau.HealthMateLC.dto.CustomerService.PharmacyDTO;
import com.LongChau.HealthMateLC.dto.CustomerService.SendEmailRequest;
import com.LongChau.HealthMateLC.model.Feedback;
import com.LongChau.HealthMateLC.model.Invoice;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.UserInformationRepository;
import com.LongChau.HealthMateLC.service.FeedbackService;
import com.LongChau.HealthMateLC.service.PharmacyService;
import com.LongChau.HealthMateLC.service.EmailService;
import com.LongChau.HealthMateLC.service.CustomerMessageService;
import com.LongChau.HealthMateLC.model.CustomerMessage;
import com.LongChau.HealthMateLC.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/customer-service")
public class CustomerServiceController {
    @Autowired
    private PharmacyService pharmacyService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private UserInformationRepository userInformationRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CustomerMessageService customerMessageService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private com.LongChau.HealthMateLC.repository.InvoiceRepository invoiceRepository;

    // #region Pharmacy
    @GetMapping("/pharmacies")
    public ResponseEntity<List<PharmacyDTO>> getAllPharmacies() {
        List<PharmacyDTO> pharmacies = pharmacyService.getAllPharmaciesCS();
        return ResponseEntity.ok(pharmacies);
    }
    // #endregion

    // #region Feedback
    // Lấy tất cả đánh giá, Id Pharmacy, Id Customer
    @GetMapping("/reviews")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacks(
            @RequestParam(required = false) Integer pharmacyId,
            @RequestParam(required = false) Integer customerId) {

        List<FeedbackDTO> feedbacks;

        // Lọc theo các tham số nếu có
        if (pharmacyId != null) {
            feedbacks = feedbackService.getFeedbackByPharmacy(pharmacyId);
        } else if (customerId != null) {
            feedbacks = feedbackService.getFeedbackByCustomer(customerId);
        } else {
            // Lấy tất cả đánh giá nếu không có tham số lọc
            feedbacks = feedbackService.getAllFeedback();
        }

        return ResponseEntity.ok(feedbacks);
    }

    // Lấy đánh giá theo id đánh giá
    @GetMapping("/reviews/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable int id) {
        Optional<FeedbackDTO> feedback = feedbackService.getFeedbackById(id);
        return feedback
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/reviews/{id}/status")
    public ResponseEntity<FeedbackDTO> updateFeedbackStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> statusUpdate) {

        String status = statusUpdate.get("status");
        String handledByUserIdStr = statusUpdate.get("handledByUserId");
        Integer handledByUserId = null;
        if (handledByUserIdStr != null) {
            try {
                handledByUserId = Integer.parseInt(handledByUserIdStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }

        Feedback updatedFeedback = feedbackService.updateFeedbackStatus(id, status, handledByUserId);
        if (updatedFeedback != null) {
            return ResponseEntity.ok(feedbackService.toDTO(updatedFeedback));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // #endregion

    // #region User Information
    @GetMapping("/user/current")
    public ResponseEntity<UserInformationCsDTO> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserInformation userInfo = userInformationRepository.findById(user.getUserId()).orElse(null);
        UserInformationCsDTO dto = new UserInformationCsDTO(
                user.getUserId(),
                userInfo != null ? userInfo.getFullName() : null,
                user.getRole(),
                userInfo != null ? userInfo.getEmail() : null,
                userInfo != null ? userInfo.getPhone() : null,
                user.getUsername(),
                user.getIsActive());
        return ResponseEntity.ok(dto);
    }
    // #endregion

    // #region Messaging/Email
    @GetMapping("/messages")
    public ResponseEntity<List<CustomerMessage>> getAllMessages() {
        List<CustomerMessage> messages = customerMessageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmailToCustomer(@RequestBody SendEmailRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Please login to send email"));
        }
        String email = request.getTo();
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or missing email address"));
        }
        try {
            emailService.sendSimpleEmail(email, request.getSubject(), request.getContent());
            // Lưu trực tiếp CustomerMessage không qua DTO trung gian
            CustomerMessage message = new CustomerMessage();
            message.setSenderId(user.getUserId());
            message.setMessageType(request.getType() != null ? request.getType() : "email");
            message.setChannel(request.getChannel() != null ? request.getChannel() : "email");
            String targetType = request.getTarget();
            if (targetType == null || targetType.trim().isEmpty()) {
                targetType = "individual";
            }
            message.setTargetType(targetType);
            message.setTargetCustomerId(request.getCustomerId());
            message.setMessageText(request.getContent());
            java.time.LocalDateTime sentAt = null;
            try {
                if (request.getSendTime() != null) {
                    sentAt = java.time.LocalDateTime.parse(request.getSendTime().replace("Z", ""));
                }
            } catch (Exception e) {
                sentAt = java.time.LocalDateTime.now();
            }
            if (sentAt == null)
                sentAt = java.time.LocalDateTime.now();
            message.setSentAt(sentAt);
            customerMessageService.saveMessage(message);
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi gửi email
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
    // #endregion

    // #region Statistics
    /**
     * Đếm tổng số tin nhắn đã gửi
     */
    @GetMapping("/stats/messages-sent")
    public ResponseEntity<Map<String, Long>> getMessagesSentCount() {
        long count = customerMessageService.countMessages();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Đếm số đánh giá cần xử lý (rating <= 3 sao)
     */
    @GetMapping("/stats/reviews-pending")
    public ResponseEntity<Map<String, Long>> getReviewsPendingCount() {
        long count = feedbackService.countPendingReviews();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Đếm tổng số khách hàng đã được phục vụ
     */
    @GetMapping("/stats/customers-served")
    public ResponseEntity<Map<String, Integer>> getCustomersServedCount() {
        int count = customerService.getNumberOfCustomers();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Lấy điểm đánh giá trung bình (từ 1-5) để tính tỷ lệ hài lòng
     */
    @GetMapping("/stats/average-rating")
    public ResponseEntity<Map<String, Double>> getAverageRating() {
        double avg = feedbackService.getAverageRating();
        return ResponseEntity.ok(Map.of("average", avg));
    }
    // #endregion

    /**
     * Lấy tất cả hóa đơn đã thanh toán trong 3 ngày gần nhất để nhắc nhở
     */
    @GetMapping("/invoices/reminders")
    public ResponseEntity<List<Map<String, Object>>> getReminderInvoices() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(3);
        List<Invoice> invoices = invoiceRepository.findByStatusAndInvoiceDateBetween(
                "paid", start, end);
        List<Map<String, Object>> reminders = invoices.stream()
                .map(inv -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("invoiceId", inv.getInvoiceId());
                    map.put("customerId", inv.getCustomer().getCustomerId());
                    map.put("notes", inv.getNotes());
                    map.put("customerEmail", inv.getCustomer().getEmail());
                    map.put("customerName", inv.getCustomer().getFullName());
                    map.put("invoiceDate", inv.getInvoiceDate());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(reminders);
    }

    /**
     * Gửi nhắc nhở uống thuốc tới tất cả khách hàng có hóa đơn đã thanh toán trong
     * vòng 3 ngày gần nhất
     */
    @PostMapping("/invoices/reminders/send")
    public ResponseEntity<Map<String, Object>> sendBulkReminders(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Please login to send reminders"));
        }
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(3);
        List<Invoice> invoices = invoiceRepository.findByStatusAndInvoiceDateBetween(
                "paid", start, end);
        int sentCount = 0;
        int failedCount = 0;
        for (Invoice inv : invoices) {
            String email = inv.getCustomer().getEmail();
            if (email == null || email.trim().isEmpty()) {
                failedCount++;
                continue;
            }
            String name = inv.getCustomer().getFullName();
            String content = String.format(
                    "Xin chào %s,\n\nLời nhắc uống thuốc:\n%s\n\nChúc bạn mau khỏe!", name, inv.getNotes());
            try {
                emailService.sendSimpleEmail(email, "Nhắc nhở uống thuốc - Long Châu", content);
                sentCount++;
            } catch (Exception e) {
                failedCount++;
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", invoices.size());
        result.put("sentCount", sentCount);
        result.put("failedCount", failedCount);
        return ResponseEntity.ok(result);
    }
}
