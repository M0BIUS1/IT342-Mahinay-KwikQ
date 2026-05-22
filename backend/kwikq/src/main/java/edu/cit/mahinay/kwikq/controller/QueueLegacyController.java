package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
public class QueueLegacyController {

    @Autowired
    private QueueService queueService;

    @PostMapping("/add/{bookId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> add(@PathVariable Long bookId) {
        try {
            User user = getAuthenticatedUser();
            return ResponseEntity.ok(queueService.addToQueue(user, bookId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{queueId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> remove(@PathVariable Long queueId) {
        try {
            User user = getAuthenticatedUser();
            queueService.removeFromQueue(user, queueId);
            return ResponseEntity.ok(new MessageResponse("Queue cancelled"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
