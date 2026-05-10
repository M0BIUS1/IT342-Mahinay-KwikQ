package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.Payment;
import edu.cit.mahinay.kwikq.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByUser(User user, Pageable pageable);
    Page<Payment> findByStatus(String status, Pageable pageable);
    List<Payment> findByUserAndStatus(User user, String status);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p WHERE p.user = ?1 AND p.status = ?2")
    Double sumAmountByUserAndStatus(User user, String status);
}
