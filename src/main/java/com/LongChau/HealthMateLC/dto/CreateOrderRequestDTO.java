package com.LongChau.HealthMateLC.dto;

import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequestDTO {
    private Integer employeeId;
    private Integer customerId;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private String invoiceDate;
    private String notes;  // Thêm trường notes

    public CreateOrderRequestDTO() {
    }

    // Getters and Setters
    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "CreateOrderRequestDTO{" +
                "employeeId=" + employeeId +
                ", customerId=" + customerId +
                ", orderItems=" + orderItems +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", invoiceDate='" + invoiceDate + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    // Inner class for order items
    public static class OrderItemDTO {
        private Integer productId;
        private Integer quantity;
        private BigDecimal unitPrice;

        public OrderItemDTO() {
        }

        public OrderItemDTO(Integer productId, Integer quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        // Getters and Setters
        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        @Override
        public String toString() {
            return "OrderItemDTO{" +
                    "productId=" + productId +
                    ", quantity=" + quantity +
                    ", unitPrice=" + unitPrice +
                    '}';
        }
    }
}
