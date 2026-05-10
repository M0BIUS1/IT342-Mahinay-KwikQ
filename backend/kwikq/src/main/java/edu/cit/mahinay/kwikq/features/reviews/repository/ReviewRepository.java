package edu.cit.mahinay.kwikq.features.reviews.repository;

import edu.cit.mahinay.kwikq.features.reviews.entity.Review;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByBook(Book book, Pageable pageable);
    Double getAverageRatingByBook(Book book);
    Long getReviewCountByBook(Book book);
}
