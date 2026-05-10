package edu.cit.mahinay.kwikq.features.reviews.controller;

import edu.cit.mahinay.kwikq.dto.ReviewResponse;
import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.books.repository.BookRepository;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.reviews.repository.ReviewRepository;
import edu.cit.mahinay.kwikq.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> addReview(@PathVariable Long bookId, @RequestParam Integer rating, @RequestParam String reviewText) {
        try {
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var review = reviewService.createReview(user, book, rating, reviewText);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ReviewResponse(review.getId(), user.getName(), rating, reviewText, review.getCreatedAt()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getBookReviews(@PathVariable Long bookId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        try {
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
            Pageable pageable = PageRequest.of(page, size);
            Page<ReviewResponse> result = reviewService.getBookReviews(book, pageable).map(r -> new ReviewResponse(r.getId(), r.getUser().getName(), r.getRating(), r.getReviewText(), r.getCreatedAt()));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/book/{bookId}/rating")
    public ResponseEntity<?> getBookRating(@PathVariable Long bookId) {
        try {
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
            Double avgRating = reviewService.getAverageRating(book);
            Long reviewCount = reviewService.getReviewCount(book);
            return ResponseEntity.ok(new MessageResponse("Rating: " + avgRating + " (" + reviewCount + " reviews)"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
