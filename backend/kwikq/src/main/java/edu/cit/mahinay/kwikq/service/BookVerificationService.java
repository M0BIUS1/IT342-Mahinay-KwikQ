package edu.cit.mahinay.kwikq.service;

import edu.cit.mahinay.kwikq.dto.BookVerificationRequest;
import edu.cit.mahinay.kwikq.dto.BookVerificationResponse;
import edu.cit.mahinay.kwikq.entity.Book;
import edu.cit.mahinay.kwikq.entity.BookVerification;
import edu.cit.mahinay.kwikq.entity.User;
import edu.cit.mahinay.kwikq.repository.BookRepository;
import edu.cit.mahinay.kwikq.repository.BookVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookVerificationService {

    @Autowired
    private BookVerificationRepository bookVerificationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public BookVerificationResponse submitBook(User librarian, BookVerificationRequest request) {
        // Check if unique code already exists
        if (bookVerificationRepository.findByUniqueCode(request.getUniqueCode()).isPresent()) {
            throw new RuntimeException("Book with this unique code already exists");
        }

        // Create verification request
        BookVerification verification = new BookVerification(
                request.getTitle(),
                request.getAuthor(),
                request.getCategory(),
                request.getUniqueCode(),
                librarian
        );
        verification.setDescription(request.getDescription());

        BookVerification saved = bookVerificationRepository.save(verification);
        return mapToResponse(saved);
    }

    @Transactional
    public BookVerificationResponse approveBook(User admin, Long verificationId) {
        BookVerification verification = bookVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("Book verification not found"));

        if (verification.getStatus() != BookVerification.VerificationStatus.PENDING) {
            throw new RuntimeException("Only pending books can be approved");
        }

        // Create actual book from verification
        Book book = new Book();
        book.setTitle(verification.getTitle());
        book.setAuthor(verification.getAuthor());
        book.setCategory(verification.getCategory());
        book.setUniqueCode(verification.getUniqueCode());

        bookRepository.save(book);

        // Update verification status
        verification.setStatus(BookVerification.VerificationStatus.APPROVED);
        verification.setVerifiedBy(admin);
        verification.setVerifiedAt(LocalDateTime.now());

        // Log audit action
        auditLogService.logAction(admin, "APPROVED_BOOK", "BookVerification", verificationId,
                "Book '" + verification.getTitle() + "' approved and added to catalog");

        BookVerification saved = bookVerificationRepository.save(verification);
        return mapToResponse(saved);
    }

    @Transactional
    public BookVerificationResponse rejectBook(User admin, Long verificationId, String reason) {
        BookVerification verification = bookVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("Book verification not found"));

        if (verification.getStatus() != BookVerification.VerificationStatus.PENDING) {
            throw new RuntimeException("Only pending books can be rejected");
        }

        verification.setStatus(BookVerification.VerificationStatus.REJECTED);
        verification.setVerifiedBy(admin);
        verification.setVerifiedAt(LocalDateTime.now());
        verification.setRejectionReason(reason);

        // Log audit action
        auditLogService.logAction(admin, "REJECTED_BOOK", "BookVerification", verificationId,
                "Book '" + verification.getTitle() + "' rejected. Reason: " + reason);

        BookVerification saved = bookVerificationRepository.save(verification);
        return mapToResponse(saved);
    }

    public Page<BookVerificationResponse> getPendingBooks(Pageable pageable) {
        return bookVerificationRepository.findByStatus(
                BookVerification.VerificationStatus.PENDING, pageable).map(this::mapToResponse);
    }

    public Page<BookVerificationResponse> getAllVerifications(Pageable pageable) {
        return bookVerificationRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<BookVerificationResponse> getLibrarianSubmissions(User librarian, Pageable pageable) {
        return bookVerificationRepository.findBySubmittedBy(librarian, pageable).map(this::mapToResponse);
    }

    public Long getPendingCount() {
        return bookVerificationRepository.countByStatus(BookVerification.VerificationStatus.PENDING);
    }

    private BookVerificationResponse mapToResponse(BookVerification verification) {
        return new BookVerificationResponse(
                verification.getId(),
                verification.getTitle(),
                verification.getAuthor(),
                verification.getCategory(),
                verification.getUniqueCode(),
                verification.getDescription(),
                verification.getSubmittedBy().getName(),
                verification.getSubmittedBy().getEmail(),
                verification.getStatus().toString(),
                verification.getRejectionReason(),
                verification.getSubmittedAt(),
                verification.getVerifiedAt()
        );
    }
}
