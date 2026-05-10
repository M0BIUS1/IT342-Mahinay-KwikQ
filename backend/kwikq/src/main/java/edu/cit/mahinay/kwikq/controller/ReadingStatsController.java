package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.service.ReadingStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
public class ReadingStatsController {

    @Autowired
    private ReadingStatsService readingStatsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getReadingStatsOverview() {
        try {
            User user = getAuthenticatedUser();
            Long totalRead = readingStatsService.getTotalBooksRead(user);
            Long activeBorrows = readingStatsService.getActiveBorrows(user);
            Long overdueCount = readingStatsService.getOverdueCount(user);
            Double avgDays = readingStatsService.getAverageDaysPerBook(user);
            String favoriteCategory = readingStatsService.getMostReadCategory(user);

            ReadingStats stats = new ReadingStats(totalRead, activeBorrows, overdueCount, avgDays, favoriteCategory);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    static class ReadingStats {
        public Long totalBooksRead;
        public Long activeBorrows;
        public Long overdueCount;
        public Double averageDaysPerBook;
        public String favoriteCategory;

        public ReadingStats(Long totalBooksRead, Long activeBorrows, Long overdueCount, Double averageDaysPerBook, String favoriteCategory) {
            this.totalBooksRead = totalBooksRead;
            this.activeBorrows = activeBorrows;
            this.overdueCount = overdueCount;
            this.averageDaysPerBook = averageDaysPerBook;
            this.favoriteCategory = favoriteCategory;
        }
    }
}
