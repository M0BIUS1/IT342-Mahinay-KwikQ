package edu.cit.mahinay.kwikq.features.reviews.entity;

import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;

    public Review() { this.createdAt = LocalDateTime.now(); }
    public Review(User user, Book book, Integer rating, String reviewText) { this(); this.user = user; this.book = book; this.rating = rating; this.reviewText = reviewText; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
