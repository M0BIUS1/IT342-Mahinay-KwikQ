package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.QueueResponse;
import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queues")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @PostMapping("/add/{bookId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> addToQueue(@PathVariable Long bookId) {
        try {
            User user = getAuthenticatedUser();
            QueueResponse resp = queueService.addToQueue(user, bookId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{queueId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> removeFromQueue(@PathVariable Long queueId) {
        try {
            User user = getAuthenticatedUser();
            queueService.removeFromQueue(user, queueId);
            return ResponseEntity.ok(new MessageResponse("Queue cancelled"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-queues")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> getMyQueues(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        try {
            User user = getAuthenticatedUser();
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(queueService.getUserQueue(user, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> getBookQueue(@PathVariable Long bookId) {
        try {
            List<QueueResponse> list = queueService.getBookQueue(bookId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/position/{bookId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> getPosition(@PathVariable Long bookId) {
        try {
            User user = getAuthenticatedUser();
            Long pos = queueService.getQueuePosition(user, bookId);
            return ResponseEntity.ok(new MessageResponse("Position: " + pos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Admin endpoints for managing queues
    @GetMapping("/admin/book/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminGetBookQueue(@PathVariable Long bookId) {
        try {
            return ResponseEntity.ok(queueService.getBookQueue(bookId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/admin/{queueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminRemoveFromQueue(@PathVariable Long queueId) {
        try {
            queueService.adminRemoveFromQueue(queueId);
            return ResponseEntity.ok(new MessageResponse("Queue entry cancelled by admin"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
