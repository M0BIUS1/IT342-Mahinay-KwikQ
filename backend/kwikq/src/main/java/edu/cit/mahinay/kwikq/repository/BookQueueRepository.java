package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.BookQueue;
import edu.cit.mahinay.kwikq.entity.Book;
import edu.cit.mahinay.kwikq.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookQueueRepository extends JpaRepository<BookQueue, Long> {
    Page<BookQueue> findByUser(User user, Pageable pageable);
    Page<BookQueue> findByUserAndStatus(User user, BookQueue.QueueStatus status, Pageable pageable);
    List<BookQueue> findByBookAndStatusOrderByQueuePosition(Book book, BookQueue.QueueStatus status);
    Optional<BookQueue> findByBookAndUserAndStatus(Book book, User user, BookQueue.QueueStatus status);
    Long countByBookAndStatus(Book book, BookQueue.QueueStatus status);
    Integer findMaxQueuePositionByBook(Book book);
}
