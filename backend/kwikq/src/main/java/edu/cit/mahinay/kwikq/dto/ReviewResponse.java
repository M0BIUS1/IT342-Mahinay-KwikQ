package edu.cit.mahinay.kwikq.dto;

import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private String userName;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;

    public ReviewResponse(Long id, String userName, Integer rating, String reviewText, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getUserName() { return userName; }
    public Integer getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
