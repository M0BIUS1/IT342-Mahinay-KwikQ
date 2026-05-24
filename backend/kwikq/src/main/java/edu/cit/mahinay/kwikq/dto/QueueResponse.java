package edu.cit.mahinay.kwikq.dto;

import java.time.LocalDateTime;

public class QueueResponse {
    private Long id;
    private String bookTitle;
    private String bookAuthor;
    private Integer queuePosition;
    private String status;
    private LocalDateTime queuedAt;
    private Long userId;
    private String userName;
    private String userEmail;

    // For compatibility with legacy frontend which expects 'requestedAt'
    public LocalDateTime getRequestedAt() {
        return queuedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.queuedAt = requestedAt;
    }

    // Constructors
    public QueueResponse() {}

    public QueueResponse(Long id, String bookTitle, String bookAuthor, Integer queuePosition,
                        String status, LocalDateTime queuedAt) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.queuePosition = queuePosition;
        this.status = status;
        this.queuedAt = queuedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getQueuedAt() {
        return queuedAt;
    }

    public void setQueuedAt(LocalDateTime queuedAt) {
        this.queuedAt = queuedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
