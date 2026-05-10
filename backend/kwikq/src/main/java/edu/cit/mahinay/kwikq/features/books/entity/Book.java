package edu.cit.mahinay.kwikq.features.books.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = "unique_code")
})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Book title is required")
    @Size(max = 200, message = "Book title must not exceed 200 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 150, message = "Author must not exceed 150 characters")
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(nullable = false)
    private String category;

    @NotBlank(message = "Unique code is required")
    @Size(max = 80, message = "Unique code must not exceed 80 characters")
    @Column(name = "unique_code", nullable = false, unique = true)
    private String uniqueCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUniqueCode() { return uniqueCode; }
    public void setUniqueCode(String uniqueCode) { this.uniqueCode = uniqueCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
