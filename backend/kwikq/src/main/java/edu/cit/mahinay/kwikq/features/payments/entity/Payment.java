package edu.cit.mahinay.kwikq.features.payments.entity;

import edu.cit.mahinay.kwikq.features.users.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double amount;
    private String description; // "Fine for book XYZ"
    private String status; // PENDING, PAID, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Payment() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Payment(User user, Double amount, String description) {
        this();
        this.user = user;
        this.amount = amount;
        this.description = description;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
