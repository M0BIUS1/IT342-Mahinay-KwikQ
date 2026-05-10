package edu.cit.mahinay.kwikq.service;

import edu.cit.mahinay.kwikq.entity.Borrowing;
import edu.cit.mahinay.kwikq.entity.User;
import edu.cit.mahinay.kwikq.repository.BorrowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ReadingStatsService {

    @Autowired
    private BorrowingRepository borrowingRepository;

    public Long getTotalBooksRead(User user) {
        return borrowingRepository.countByUserAndStatus(user, Borrowing.BorrowStatus.RETURNED);
    }

    public Long getActiveBorrows(User user) {
        return borrowingRepository.countByUserAndStatus(user, Borrowing.BorrowStatus.ACTIVE);
    }

    public Long getOverdueCount(User user) {
        return borrowingRepository.countByUserAndStatus(user, Borrowing.BorrowStatus.OVERDUE);
    }

    public Double getAverageDaysPerBook(User user) {
        var borrowings = borrowingRepository.findByUserAndStatus(user, Borrowing.BorrowStatus.RETURNED, null);
        if (borrowings.isEmpty()) return 0.0;

        long totalDays = 0;
        int count = 0;
        for (Borrowing b : borrowings) {
            if (b.getBorrowedAt() != null && b.getReturnedAt() != null) {
                totalDays += ChronoUnit.DAYS.between(b.getBorrowedAt(), b.getReturnedAt());
                count++;
            }
        }
        return count > 0 ? (double) totalDays / count : 0.0;
    }

    public String getMostReadCategory(User user) {
        // Placeholder - would need category tracking
        return "Fiction";
    }
}
