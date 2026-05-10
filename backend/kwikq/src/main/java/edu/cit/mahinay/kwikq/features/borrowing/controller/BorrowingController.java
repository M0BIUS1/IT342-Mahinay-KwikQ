package edu.cit.mahinay.kwikq.features.borrowing.controller;

import edu.cit.mahinay.kwikq.dto.BorrowingResponse;
import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.borrowing.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrowings")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN','LIBRARIAN')")
public class BorrowingController {

    @Autowired
    private BorrowingService borrowingService;

    @PostMapping("/borrow/{bookCopyId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookCopyId) {
        try {
            User user = getAuthenticatedUser();
            BorrowingResponse response = borrowingService.borrowBook(user, bookCopyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/return/{borrowingId}")
    public ResponseEntity<?> returnBook(@PathVariable Long borrowingId) {
        try {
            User user = getAuthenticatedUser();
            BorrowingResponse response = borrowingService.returnBook(user, borrowingId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/renew/{borrowingId}")
    public ResponseEntity<?> renewBook(@PathVariable Long borrowingId) {
        try {
            User user = getAuthenticatedUser();
            BorrowingResponse response = borrowingService.renewBook(user, borrowingId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBorrowingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = getAuthenticatedUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<BorrowingResponse> history = borrowingService.getUserBorrowingHistory(user, pageable);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveBorrowings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = getAuthenticatedUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<BorrowingResponse> active = borrowingService.getActiveBorrowings(user, pageable);
            return ResponseEntity.ok(active);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
