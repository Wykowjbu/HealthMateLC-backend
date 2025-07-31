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
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.LongChau.HealthMateLC.dto.FeedbackRequest;
import com.LongChau.HealthMateLC.model.Feedback;
import com.LongChau.HealthMateLC.model.Invoice;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.UserInformationRepository;
import com.LongChau.HealthMateLC.service.FeedbackService;
import com.LongChau.HealthMateLC.service.PharmacyService;
import com.LongChau.HealthMateLC.service.EmailService;
import com.LongChau.HealthMateLC.service.CustomerMessageService;
import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.model.CustomerMessage;
import com.LongChau.HealthMateLC.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/customer-service")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:8080" }, allowCredentials = "true")
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
            status = "pending";
        }

        Feedback updatedFeedback = feedbackService.updateFeedbackStatus(id, status, handledByUserId);
        if (updatedFeedback != null) {
            return ResponseEntity.ok(feedbackService.toDTO(updatedFeedback));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reviews")
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackRequest request) {
        Feedback feedback = feedbackService.createFeedback(request);
        FeedbackDTO dto = feedbackService.toDTO(feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    // #endregion

    // #region User Information
    @GetMapping("/user/current")
    public ResponseEntity<UserInformationCsDTO> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser"); // Sửa key từ "user" thành "currentUser"
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
        User user = (User) session.getAttribute("currentUser");
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
    
    /**
     * Lấy hóa đơn gần nhất đã thanh toán (có notes) của một khách hàng
     */
    @GetMapping("/invoices/reminders")
    public ResponseEntity<Map<String, Object>> getLatestReminderInvoice(
            @RequestParam("customerId") Integer customerId) {
        List<Invoice> invoices = invoiceRepository.findByCustomerCustomerIdAndStatusOrderByInvoiceDateDesc(customerId,
                "paid");
        Optional<Invoice> latestInvoice = invoices.stream()
                .filter(inv -> inv.getNotes() != null && !inv.getNotes().trim().isEmpty())
                .findFirst();
        if (latestInvoice.isPresent()) {
            Invoice inv = latestInvoice.get();
            Map<String, Object> map = new HashMap<>();
            map.put("invoiceId", inv.getInvoiceId());
            map.put("customerId", inv.getCustomer().getCustomerId());
            map.put("notes", inv.getNotes());
            map.put("customerEmail", inv.getCustomer().getEmail());
            map.put("customerName", inv.getCustomer().getFullName());
            map.put("invoiceDate", inv.getInvoiceDate());
            map.put("purchaseDate", inv.getInvoiceDate());
            map.put("status", inv.getStatus());
            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.ok(new HashMap<>()); // Trả về object rỗng nếu không có hóa đơn phù hợp
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

    
    // #region Customers
    /**
     * Lấy tất cả khách hàng
     */
    @GetMapping("/customers")
    public ResponseEntity<List<com.LongChau.HealthMateLC.model.Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAll();
        return ResponseEntity.ok(customers);
    }
    // #endregion

    // #region Revenue Report
    /**
     * Báo cáo doanh thu: tổng doanh thu, doanh thu theo từng nhà thuốc, số đơn hàng
     */
    @GetMapping("/stats/revenue-report")
    public ResponseEntity<Map<String, Object>> getRevenueReport(
            @RequestParam(value = "pharmacyId", required = false) Integer pharmacyId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        List<Invoice> invoices;
        // Parse time range if provided
        java.time.LocalDateTime start = null, end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = java.time.LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } catch (Exception e) {
            // ignore parse error, fallback to all
        }
        if (pharmacyId != null && pharmacyId > 0) {
            if (start != null && end != null) {
                invoices = invoiceRepository.findByPharmacyPharmacyIdAndStatusAndInvoiceDateBetween(pharmacyId, "paid",
                        start, end);
            } else {
                invoices = invoiceRepository.findByPharmacyPharmacyIdAndStatus(pharmacyId, "paid");
            }
        } else {
            if (start != null && end != null) {
                invoices = invoiceRepository.findByStatusAndInvoiceDateBetween("paid", start, end);
            } else {
                invoices = invoiceRepository.findByStatus("paid");
            }
        }
        double totalRevenue = invoices.stream().mapToDouble(inv -> inv.getTotalAmount().doubleValue()).sum();
        Map<Integer, Double> revenueByPharmacy = new HashMap<>();
        Map<Integer, Integer> orderCountByPharmacy = new HashMap<>();
        for (Invoice inv : invoices) {
            int pid = inv.getPharmacy().getPharmacyId();
            revenueByPharmacy.put(pid, revenueByPharmacy.getOrDefault(pid, 0.0) + inv.getTotalAmount().doubleValue());
            orderCountByPharmacy.put(pid, orderCountByPharmacy.getOrDefault(pid, 0) + 1);
        }
        // Lấy tên nhà thuốc
        List<PharmacyDTO> pharmacies = pharmacyService.getAllPharmaciesCS();
        List<Map<String, Object>> pharmacyReports = pharmacies.stream().map(ph -> {
            Map<String, Object> map = new HashMap<>();
            map.put("pharmacyId", ph.getId());
            map.put("pharmacyName", ph.getName());
            map.put("revenue", revenueByPharmacy.getOrDefault(ph.getId(), 0.0));
            map.put("orderCount", orderCountByPharmacy.getOrDefault(ph.getId(), 0));
            return map;
        }).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("pharmacyReports", pharmacyReports);
        result.put("orderCount", invoices.size());
        return ResponseEntity.ok(result);
    }
    // #endregion
}
