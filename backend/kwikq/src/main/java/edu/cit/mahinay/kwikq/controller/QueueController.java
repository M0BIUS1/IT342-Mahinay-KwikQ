package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.QueueResponse;
import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.entity.User;
import edu.cit.mahinay.kwikq.service.QueueService;
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

import java.util.List;

@RestController
@RequestMapping("/api/queues")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN','LIBRARIAN')")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @PostMapping("/add/{bookId}")
    public ResponseEntity<?> addToQueue(@PathVariable Long bookId) {
        try {
            User user = getAuthenticatedUser();
            QueueResponse response = queueService.addToQueue(user, bookId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{queueId}")
    public ResponseEntity<?> removeFromQueue(@PathVariable Long queueId) {
        try {
            User user = getAuthenticatedUser();
            queueService.removeFromQueue(user, queueId);
            return ResponseEntity.ok(new MessageResponse("Removed from queue"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-queues")
    public ResponseEntity<?> getMyQueues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = getAuthenticatedUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<QueueResponse> queues = queueService.getUserQueue(user, pageable);
            return ResponseEntity.ok(queues);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getBookQueue(@PathVariable Long bookId) {
        try {
            List<QueueResponse> queue = queueService.getBookQueue(bookId);
            return ResponseEntity.ok(queue);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/position/{bookId}")
    public ResponseEntity<?> getQueuePosition(@PathVariable Long bookId) {
        try {
            User user = getAuthenticatedUser();
            Long position = queueService.getQueuePosition(user, bookId);
            return ResponseEntity.ok(new MessageResponse("Queue position: " + position));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
