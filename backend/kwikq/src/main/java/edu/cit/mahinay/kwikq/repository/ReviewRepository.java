package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.Review;
import edu.cit.mahinay.kwikq.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByBook(Book book, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book = ?1")
    Double getAverageRatingByBook(Book book);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book = ?1")
    Long getReviewCountByBook(Book book);
}
