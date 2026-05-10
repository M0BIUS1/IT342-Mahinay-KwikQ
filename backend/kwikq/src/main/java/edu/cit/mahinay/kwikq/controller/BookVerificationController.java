package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.BookVerificationRequest;
import edu.cit.mahinay.kwikq.dto.BookVerificationResponse;
import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.verification.service.BookVerificationService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/book-verification")
public class BookVerificationController {

    @Autowired
    private BookVerificationService bookVerificationService;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> submitBook(@Valid @RequestBody BookVerificationRequest request) {
        try {
            User user = getAuthenticatedUser();
            BookVerificationResponse response = bookVerificationService.submitBook(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/approve/{verificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveBook(@PathVariable Long verificationId) {
        try {
            User user = getAuthenticatedUser();
            BookVerificationResponse response = bookVerificationService.approveBook(user, verificationId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/reject/{verificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectBook(
            @PathVariable Long verificationId,
            @RequestParam String reason) {
        try {
            User user = getAuthenticatedUser();
            BookVerificationResponse response = bookVerificationService.rejectBook(user, verificationId, reason);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BookVerificationResponse> result = bookVerificationService.getPendingBooks(pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllVerifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BookVerificationResponse> result = bookVerificationService.getAllVerifications(pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-submissions")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> getMySubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = getAuthenticatedUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<BookVerificationResponse> result = bookVerificationService.getLibrarianSubmissions(user, pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/pending-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingCount() {
        try {
            Long count = bookVerificationService.getPendingCount();
            return ResponseEntity.ok(new MessageResponse("Pending: " + count));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
