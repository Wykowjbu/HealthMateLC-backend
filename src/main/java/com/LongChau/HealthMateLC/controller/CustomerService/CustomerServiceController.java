package com.LongChau.HealthMateLC.controller.CustomerService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LongChau.HealthMateLC.dto.CustomerService.UserInformationCsDTO;
import com.LongChau.HealthMateLC.dto.CustomerService.FeedbackDTO;
import com.LongChau.HealthMateLC.dto.CustomerService.PharmacyDTO;
import com.LongChau.HealthMateLC.model.Feedback;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.UserInformationRepository;
import com.LongChau.HealthMateLC.service.FeedbackService;
import com.LongChau.HealthMateLC.service.PharmacyService;

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
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }

        Feedback updatedFeedback = feedbackService.updateFeedbackStatus(id, status);
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

}
