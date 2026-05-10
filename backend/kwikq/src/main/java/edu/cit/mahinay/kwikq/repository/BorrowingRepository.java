package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.Borrowing;
import edu.cit.mahinay.kwikq.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Page<Borrowing> findByUser(User user, Pageable pageable);
    Page<Borrowing> findByUserAndStatus(User user, Borrowing.BorrowStatus status, Pageable pageable);
    List<Borrowing> findByUserAndStatus(User user, Borrowing.BorrowStatus status);
    List<Borrowing> findByStatusAndDueDateBefore(Borrowing.BorrowStatus status, LocalDateTime date);
    Long countByUserAndStatus(User user, Borrowing.BorrowStatus status);
}
