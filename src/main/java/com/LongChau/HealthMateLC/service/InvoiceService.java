package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.*;
import com.LongChau.HealthMateLC.dto.CreateOrderRequestDTO;
import com.LongChau.HealthMateLC.dto.InvoiceResponseDTO;
import com.LongChau.HealthMateLC.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private LoyaltyPointRepository loyaltyPointRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getById(Integer id) {
        return invoiceRepository.findById(id);
    }

    public List<Invoice> getOrderHistoryByCustomerId(Integer customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }

    public List<InvoiceResponseDTO> getOrderHistoryDTOByCustomerId(Integer customerId) {
        List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public void deleteById(Integer id) {
        invoiceRepository.deleteById(id);
    }

    @Transactional
    public InvoiceResponseDTO createOrder(CreateOrderRequestDTO orderRequest) {
        // Lấy thông tin user/employee
        User user = userRepository.findById(orderRequest.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Lấy thông tin customer (có thể null cho khách guest)
        Customer customer = null;
        if (orderRequest.getCustomerId() != null) {
            customer = customerRepository.findById(orderRequest.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        // Lấy pharmacy từ user information
        Pharmacy pharmacy = user.getUserInformation().getPharmacy();
        if (pharmacy == null) {
            throw new RuntimeException("Employee is not assigned to any pharmacy");
        }

        // Parse ISO date string to LocalDateTime
        LocalDateTime invoiceDateTime = LocalDateTime.now();
        if (orderRequest.getInvoiceDate() != null && !orderRequest.getInvoiceDate().isEmpty()) {
            try {
                invoiceDateTime = LocalDateTime.parse(orderRequest.getInvoiceDate().replace("Z", ""));
            } catch (Exception e) {
                // If parsing fails, use current time
                invoiceDateTime = LocalDateTime.now();
            }
        }

        // Tính điểm loyalty: 1 điểm cho mỗi 10,000 VND
        Integer pointsEarned = calculateLoyaltyPoints(orderRequest.getTotalAmount());
        // Lưu điểm loyalty chỉ khi có customer (không phải guest)
        if (pointsEarned > 0 && customer != null) {
            // Tạo bản ghi mới trong bảng LoyaltyPoints
            LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
            loyaltyPoint.setCustomer(customer);
            loyaltyPoint.setPoints(pointsEarned);
            loyaltyPointRepository.save(loyaltyPoint);

            // Cập nhật tổng điểm trong bảng Customer
            customer.setTotalPoints(customer.getTotalPoints() + pointsEarned);
            customerRepository.save(customer);
        }

        // Tạo Invoice
        Invoice invoice = new Invoice();
        invoice.setPharmacy(pharmacy);
        invoice.setCustomer(customer);
        invoice.setUser(user);
        invoice.setInvoiceDate(invoiceDateTime);
        invoice.setTotalAmount(orderRequest.getTotalAmount());
        invoice.setPayment(orderRequest.getPaymentMethod());
        invoice.setStatus(orderRequest.getStatus() != null ? orderRequest.getStatus() : "pending");
        invoice.setPointsEarned(customer != null ? pointsEarned : 0); // Guest không tích điểm
        invoice.setNotes(orderRequest.getNotes());

        // Lưu Invoice trước để lấy ID
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Tạo và lưu InvoiceDetails
        List<InvoiceDetail> invoiceDetails = new ArrayList<>();
        for (CreateOrderRequestDTO.OrderItemDTO item : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(savedInvoice);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getUnitPrice());

            invoiceDetails.add(detail);
        }

        // Lưu tất cả invoice details
        invoiceDetailRepository.saveAll(invoiceDetails);

        // Set invoice details vào invoice
        savedInvoice.setInvoiceDetails(invoiceDetails);

        return convertToDTO(savedInvoice);
    }

    public List<InvoiceResponseDTO> getAllInvoicesWithDetails() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tính điểm loyalty dựa trên tổng tiền
     * Quy tắc: 1 điểm cho mỗi 10,000 VND
     * @param totalAmount Tổng tiền của hóa đơn
     * @return Số điểm loyalty được tích
     */
    private Integer calculateLoyaltyPoints(BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // Chia tổng tiền cho 10,000 và lấy phần nguyên
        BigDecimal pointsDecimal = totalAmount.divide(new BigDecimal("10000"), 0, BigDecimal.ROUND_DOWN);
        return pointsDecimal.intValue();
    }

    private InvoiceResponseDTO convertToDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setInvoiceId(invoice.getInvoiceId());

        // Safely access pharmacy data
        if (invoice.getPharmacy() != null) {
            dto.setPharmacyId(invoice.getPharmacy().getPharmacyId());
            dto.setPharmacyName(invoice.getPharmacy().getPharmacyName());
        }

        // Safely access customer data
        if (invoice.getCustomer() != null) {
            dto.setCustomerId(invoice.getCustomer().getCustomerId());
            dto.setCustomerName(invoice.getCustomer().getFullName());
        }

        // Safely access user data
        if (invoice.getUser() != null) {
            dto.setEmployeeId(invoice.getUser().getUserId());
            dto.setEmployeeName(invoice.getUser().getUserInformation().getFullName());
        }

        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setPointsEarned(invoice.getPointsEarned());
        dto.setPayment(invoice.getPayment());
        dto.setStatus(invoice.getStatus());
        dto.setNotes(invoice.getNotes());  // Thêm mapping cho notes

        // Convert invoice details
        if (invoice.getInvoiceDetails() != null) {
            List<InvoiceResponseDTO.InvoiceDetailResponseDTO> detailDTOs = invoice.getInvoiceDetails().stream()
                .map(detail -> {
                    InvoiceResponseDTO.InvoiceDetailResponseDTO detailDTO = new InvoiceResponseDTO.InvoiceDetailResponseDTO();
                    detailDTO.setInvoiceDetailId(detail.getInvoiceDetailId());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setPrice(detail.getPrice());

                    if (detail.getProduct() != null) {
                        detailDTO.setProductId(detail.getProduct().getProductId());
                        detailDTO.setProductName(detail.getProduct().getProductName());
                    }

                    return detailDTO;
                })
                .collect(Collectors.toList());
            dto.setInvoiceDetails(detailDTOs);
        }

        return dto;
    }

    public void updatePaymentStatus(Integer orderCode, String status) {
        Invoice invoice = invoiceRepository.findByOrderCode(orderCode);
        if (invoice!=null){
            invoice.setStatus(status);
            invoiceRepository.save(invoice);
        } else {
            throw new RuntimeException("Invoice not found for order code: " + orderCode);
        }
    }
}
