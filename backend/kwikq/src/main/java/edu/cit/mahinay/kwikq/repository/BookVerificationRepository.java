package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.features.verification.entity.BookVerification;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookVerificationRepository extends JpaRepository<BookVerification, Long> {
    Page<BookVerification> findByStatus(BookVerification.VerificationStatus status, Pageable pageable);
    Page<BookVerification> findBySubmittedBy(User user, Pageable pageable);
    Optional<BookVerification> findByUniqueCode(String uniqueCode);
    Long countByStatus(BookVerification.VerificationStatus status);
}
