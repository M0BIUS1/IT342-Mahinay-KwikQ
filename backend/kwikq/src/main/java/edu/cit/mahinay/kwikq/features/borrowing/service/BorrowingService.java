package edu.cit.mahinay.kwikq.features.borrowing.service;

import edu.cit.mahinay.kwikq.dto.BorrowingResponse;
import edu.cit.mahinay.kwikq.features.borrowing.entity.Borrowing;
import edu.cit.mahinay.kwikq.features.books.entity.BookCopy;
import edu.cit.mahinay.kwikq.features.books.repository.BookCopyRepository;
import edu.cit.mahinay.kwikq.features.borrowing.repository.BorrowingRepository;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.users.entity.UserProfile;
import edu.cit.mahinay.kwikq.features.users.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowingService {

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 10.0;

    @Transactional
    public BorrowingResponse borrowBook(User user, Long bookCopyId) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        if (profile.getIsBlocked()) throw new RuntimeException("User account is blocked");
        if (profile.getActiveBorrows() >= profile.getBorrowingLimit()) throw new RuntimeException("Borrowing limit reached");

        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new RuntimeException("Book copy not found"));

        if (bookCopy.getStatus() != BookCopy.CopyStatus.AVAILABLE) throw new RuntimeException("Book copy is not available");

        LocalDateTime dueDate = LocalDateTime.now().plusDays(LOAN_PERIOD_DAYS);
        Borrowing borrowing = new Borrowing(user, bookCopy, dueDate);

        bookCopy.setStatus(BookCopy.CopyStatus.BORROWED);
        bookCopy.setBorrowedBy(user);
        bookCopy.setBorrowedAt(LocalDateTime.now());
        bookCopy.setDueDate(dueDate);

        profile.setActiveBorrows(profile.getActiveBorrows() + 1);

        Borrowing saved = borrowingRepository.save(borrowing);
        bookCopyRepository.save(bookCopy);
        userProfileRepository.save(profile);

        return mapToResponse(saved);
    }

    @Transactional
    public BorrowingResponse returnBook(User user, Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));

        if (!borrowing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to return this book");
        }

        if (borrowing.getStatus() == Borrowing.BorrowStatus.RETURNED) {
            throw new RuntimeException("Book already returned");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(borrowing.getDueDate())) {
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(borrowing.getDueDate(), now);
            double fine = daysOverdue * FINE_PER_DAY;
            borrowing.setFineAmount(fine);

            UserProfile profile = userProfileRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("User profile not found"));
            profile.setTotalFines(profile.getTotalFines() + fine);
            userProfileRepository.save(profile);
        }

        borrowing.setReturnedAt(now);
        borrowing.setStatus(Borrowing.BorrowStatus.RETURNED);

        BookCopy bookCopy = borrowing.getBookCopy();
        bookCopy.setStatus(BookCopy.CopyStatus.AVAILABLE);
        bookCopy.setBorrowedBy(null);
        bookCopy.setBorrowedAt(null);
        bookCopy.setDueDate(null);

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        profile.setActiveBorrows(profile.getActiveBorrows() - 1);

        Borrowing saved = borrowingRepository.save(borrowing);
        bookCopyRepository.save(bookCopy);
        userProfileRepository.save(profile);

        return mapToResponse(saved);
    }

    @Transactional
    public BorrowingResponse renewBook(User user, Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));

        if (!borrowing.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized to renew this book");
        if (borrowing.getStatus() != Borrowing.BorrowStatus.ACTIVE) throw new RuntimeException("Only active borrowings can be renewed");

        LocalDateTime newDueDate = borrowing.getDueDate().plusDays(LOAN_PERIOD_DAYS);
        borrowing.setDueDate(newDueDate);

        BookCopy bookCopy = borrowing.getBookCopy();
        bookCopy.setDueDate(newDueDate);
        bookCopyRepository.save(bookCopy);

        Borrowing saved = borrowingRepository.save(borrowing);
        return mapToResponse(saved);
    }

    public Page<BorrowingResponse> getUserBorrowingHistory(User user, Pageable pageable) {
        return borrowingRepository.findByUser(user, pageable).map(this::mapToResponse);
    }

    public Page<BorrowingResponse> getActiveBorrowings(User user, Pageable pageable) {
        return borrowingRepository.findByUserAndStatus(user, Borrowing.BorrowStatus.ACTIVE, pageable)
                .map(this::mapToResponse);
    }

    public java.util.List<BorrowingResponse> getOverdueBooks() {
        java.util.List<Borrowing> overdue = borrowingRepository.findByStatusAndDueDateBefore(
                Borrowing.BorrowStatus.ACTIVE, LocalDateTime.now());
        return overdue.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private BorrowingResponse mapToResponse(Borrowing borrowing) {
        return new BorrowingResponse(
                borrowing.getId(),
                borrowing.getBookCopy().getBook().getTitle(),
                borrowing.getBookCopy().getBook().getAuthor(),
                borrowing.getBookCopy().getCopyCode(),
                borrowing.getBorrowedAt(),
                borrowing.getDueDate(),
                borrowing.getReturnedAt(),
                borrowing.getStatus().toString(),
                borrowing.getFineAmount()
        );
    }
}
