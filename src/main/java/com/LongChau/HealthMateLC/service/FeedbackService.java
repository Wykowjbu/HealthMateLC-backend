package com.LongChau.HealthMateLC.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.LongChau.HealthMateLC.repository.FeedbackRepository;
import com.LongChau.HealthMateLC.dto.CustomerService.FeedbackDTO;
import com.LongChau.HealthMateLC.dto.CustomerService.PharmacyDTO;
import com.LongChau.HealthMateLC.model.Feedback;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private com.LongChau.HealthMateLC.repository.UserRepository userRepository;

    // Lấy tất cả đánh giá
    public List<FeedbackDTO> getAllFeedback() {
        return feedbackRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FeedbackDTO toDTO(Feedback entity) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(entity.getFeedbackId());
        dto.setContent(entity.getComment());
        dto.setRating(entity.getRating());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getHandledDate());

        // Chuyển đổi Pharmacy
        if (entity.getPharmacy() != null) {
            PharmacyDTO pharmacyDTO = new PharmacyDTO();
            pharmacyDTO.setId(entity.getPharmacy().getPharmacyId());
            pharmacyDTO.setName(entity.getPharmacy().getPharmacyName());
            pharmacyDTO.setAddress(entity.getPharmacy().getAddress());
            pharmacyDTO.setPhone(entity.getPharmacy().getPhone());
            dto.setPharmacy(pharmacyDTO);
        }

        // Chuyển đổi Customer
        if (entity.getCustomer() != null) {
            FeedbackDTO.CustomerDTO customerDTO = new FeedbackDTO.CustomerDTO();
            customerDTO.setId(entity.getCustomer().getCustomerId());
            customerDTO.setFullname(entity.getCustomer().getFullName());
            customerDTO.setPhone(entity.getCustomer().getPhone());
            customerDTO.setEmail(entity.getCustomer().getEmail());
            dto.setCustomer(customerDTO);
        }
        return dto;
    }

    // Lấy đánh giá theo ID
    public Optional<FeedbackDTO> getFeedbackById(int id) {
        return feedbackRepository.findById(id)
                .map(this::toDTO);
    }

    // Lấy đánh giá theo ID nhà thuốc
    public List<FeedbackDTO> getFeedbackByPharmacy(int pharmacyId) {
        return feedbackRepository.findByPharmacy_PharmacyId(pharmacyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Lấy đánh giá theo ID khách hàng
    public List<FeedbackDTO> getFeedbackByCustomer(int customerId) {
        return feedbackRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Cập nhật trạng thái đánh giá
    public Feedback updateFeedbackStatus(int id, String status) {
        return updateFeedbackStatus(id, status, null);
    }

    // Cập nhật trạng thái đánh giá, handledByUserId
    public Feedback updateFeedbackStatus(int id, String status, Integer handledByUserId) {
        Optional<Feedback> reviewOpt = feedbackRepository.findById(id);
        if (reviewOpt.isPresent()) {
            Feedback feedback = reviewOpt.get();
            feedback.setStatus(status);
            if (("APPROVED".equals(status) || "REJECTED".equals(status)) && handledByUserId != null) {
                // Validate user
                Optional<com.LongChau.HealthMateLC.model.User> userOpt = userRepository.findById(handledByUserId);
                if (userOpt.isEmpty()) {
                    throw new IllegalArgumentException("User không tồn tại");
                }
                feedback.setHandledByUser(userOpt.get());
                feedback.setHandledDate(LocalDateTime.now());
            }
            return feedbackRepository.save(feedback);
        }
        return null;
    }

}
