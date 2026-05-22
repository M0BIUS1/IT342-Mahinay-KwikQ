package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.PaymentResponse;
import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.payments.entity.Payment;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.payments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay/{paymentId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> markAsPaid(@PathVariable Long paymentId) {
        try {
            var response = paymentService.markAsPaid(paymentId);
            return ResponseEntity.ok(mapToResponse(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-payments")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> getMyPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = getAuthenticatedUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<PaymentResponse> result = paymentService.getUserPayments(user, pageable)
                    .map(this::mapToResponse);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/pending-amount")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<?> getPendingAmount() {
        try {
            User user = getAuthenticatedUser();
            Double amount = paymentService.getUserPendingAmount(user);
            return ResponseEntity.ok(new MessageResponse("Pending: $" + amount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getAmount(), payment.getDescription(),
                payment.getStatus(), payment.getCreatedAt(), payment.getPaidAt());
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
