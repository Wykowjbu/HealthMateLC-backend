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
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.Invoice;
import com.LongChau.HealthMateLC.repository.CustomerRepository;
import com.LongChau.HealthMateLC.repository.PharmacyRepository;
import com.LongChau.HealthMateLC.repository.InvoiceRepository;
import com.LongChau.HealthMateLC.dto.FeedbackRequest;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private com.LongChau.HealthMateLC.repository.UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

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
        dto.setCreatedAt(entity.getFeedbackDate());

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
                Optional<User> userOpt = userRepository.findById(handledByUserId);
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

    /**
     * Đếm số đánh giá cần xử lý (rating <= 3)
     */
    public long countPendingReviews() {
        return feedbackRepository.countByRatingLessThanEqual(3);
    }

    /**
     * Tính rating trung bình từ tất cả đánh giá
     */
    public double getAverageRating() {
        List<Feedback> all = feedbackRepository.findAll();
        if (all.isEmpty())
            return 0.0;
        double sum = all.stream().mapToInt(Feedback::getRating).sum();
        return sum / all.size();
    }

    /**
     * Create a new Feedback from request payload
     */
    public Feedback createFeedback(FeedbackRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Pharmacy pharmacy = pharmacyRepository.findById(request.getPharmacyId())
                .orElseThrow(() -> new IllegalArgumentException("Pharmacy not found"));
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setPharmacy(pharmacy);
        feedback.setInvoice(invoice);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setStatus("REJECTED"); // Ensure status is always set
        return feedbackRepository.save(feedback);
    }

}
