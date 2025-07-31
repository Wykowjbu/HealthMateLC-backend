package com.LongChau.HealthMateLC.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceDTO {
    private Integer invoiceId;
    private String pharmacyName;
    private String userName;
    private String customerName;
    private List<InvoiceDetailDTO> invoiceDetails;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private LocalDateTime invoiceDate;
    private String status;

    public InvoiceDTO() {
    }

    public InvoiceDTO(Integer invoiceId, String pharmacyName, String userName, String customerName,
                      List<InvoiceDetailDTO> invoiceDetails, BigDecimal totalPrice, String paymentMethod) {
        this.invoiceId = invoiceId;
        this.pharmacyName = pharmacyName;
        this.userName = userName;
        this.customerName = customerName;
        this.invoiceDetails = invoiceDetails;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    public InvoiceDTO(Integer invoiceId, String pharmacyName, String userName, String customerName,
                      List<InvoiceDetailDTO> invoiceDetails, BigDecimal totalPrice, String paymentMethod,
                      LocalDateTime invoiceDate, String status) {
        this.invoiceId = invoiceId;
        this.pharmacyName = pharmacyName;
        this.userName = userName;
        this.customerName = customerName;
        this.invoiceDetails = invoiceDetails;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.invoiceDate = invoiceDate;
        this.status = status;
    }

    // Getters and Setters
    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<InvoiceDetailDTO> getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(List<InvoiceDetailDTO> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InvoiceDTO{" +
                "invoiceId=" + invoiceId +
                ", pharmacyName='" + pharmacyName + '\'' +
                ", userName='" + userName + '\'' +
                ", customerName='" + customerName + '\'' +
                ", invoiceDetails=" + invoiceDetails +
                ", totalPrice=" + totalPrice +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", status='" + status + '\'' +
                '}';
    }
}