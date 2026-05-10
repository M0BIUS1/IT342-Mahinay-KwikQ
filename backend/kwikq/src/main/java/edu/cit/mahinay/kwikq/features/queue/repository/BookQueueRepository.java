package edu.cit.mahinay.kwikq.features.queue.repository;

import edu.cit.mahinay.kwikq.features.queue.entity.BookQueue;
import edu.cit.mahinay.kwikq.features.books.entity.Book;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookQueueRepository extends JpaRepository<BookQueue, Long> {
    Optional<BookQueue> findByBookAndUserAndStatus(Book book, User user, BookQueue.QueueStatus status);
    Integer findMaxQueuePositionByBook(Book book);
    List<BookQueue> findByBookAndStatusOrderByQueuePosition(Book book, BookQueue.QueueStatus status);
    Page<BookQueue> findByUserAndStatus(User user, BookQueue.QueueStatus status, Pageable pageable);
    Page<BookQueue> findByBookAndStatus(Book book, BookQueue.QueueStatus status, Pageable pageable);
    List<BookQueue> findByBookAndStatus(Book book, BookQueue.QueueStatus status);
    Long countByBookAndStatus(Book book, BookQueue.QueueStatus status);
}
