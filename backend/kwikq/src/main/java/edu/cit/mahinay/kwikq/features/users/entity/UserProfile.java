package edu.cit.mahinay.kwikq.features.users.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "borrowing_limit")
    private Integer borrowingLimit = 5;

    @Column(name = "active_borrows")
    private Integer activeBorrows = 0;

    @Column(name = "total_fines", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double totalFines = 0.0;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UserProfile() {}

    public UserProfile(User user) {
        this.user = user;
        this.borrowingLimit = 5;
        this.activeBorrows = 0;
        this.totalFines = 0.0;
        this.isBlocked = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getBorrowingLimit() { return borrowingLimit; }
    public void setBorrowingLimit(Integer borrowingLimit) { this.borrowingLimit = borrowingLimit; }
    public Integer getActiveBorrows() { return activeBorrows; }
    public void setActiveBorrows(Integer activeBorrows) { this.activeBorrows = activeBorrows; }
    public Double getTotalFines() { return totalFines; }
    public void setTotalFines(Double totalFines) { this.totalFines = totalFines; }
    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
