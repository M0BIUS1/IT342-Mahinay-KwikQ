package edu.cit.mahinay.kwikq.dto;

import java.time.LocalDateTime;

public class PaymentResponse {
    private Long id;
    private Double amount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public PaymentResponse(Long id, Double amount, String description, String status, LocalDateTime createdAt, LocalDateTime paidAt) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    // Getters
    public Long getId() { return id; }
    public Double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
}
