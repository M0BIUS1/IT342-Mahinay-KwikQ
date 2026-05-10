package edu.cit.mahinay.kwikq.features.payments.service;

import edu.cit.mahinay.kwikq.features.payments.entity.Payment;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.payments.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment createPayment(User user, Double amount, String description) {
        Payment payment = new Payment(user, amount, description);
        return paymentRepository.save(payment);
    }

    public Payment markAsPaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus("PAID");
        payment.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public Page<Payment> getUserPayments(User user, Pageable pageable) {
        return paymentRepository.findByUser(user, pageable);
    }

    public Page<Payment> getPendingPayments(Pageable pageable) {
        return paymentRepository.findByStatus("PENDING", pageable);
    }

    public Double getUserPendingAmount(User user) {
        Double amount = paymentRepository.sumAmountByUserAndStatus(user, "PENDING");
        return amount != null ? amount : 0.0;
    }
}
