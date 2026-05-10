package edu.cit.mahinay.kwikq.service;

import edu.cit.mahinay.kwikq.entity.Review;
import edu.cit.mahinay.kwikq.entity.Book;
import edu.cit.mahinay.kwikq.entity.User;
import edu.cit.mahinay.kwikq.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(User user, Book book, Integer rating, String reviewText) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        Review review = new Review(user, book, rating, reviewText);
        return reviewRepository.save(review);
    }

    public Page<Review> getBookReviews(Book book, Pageable pageable) {
        return reviewRepository.findByBook(book, pageable);
    }

    public Double getAverageRating(Book book) {
        Double avg = reviewRepository.getAverageRatingByBook(book);
        return avg != null ? avg : 0.0;
    }

    public Long getReviewCount(Book book) {
        return reviewRepository.getReviewCountByBook(book);
    }
}
