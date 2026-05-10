package edu.cit.mahinay.kwikq.repository;

import edu.cit.mahinay.kwikq.entity.Notification;
import edu.cit.mahinay.kwikq.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser(User user, Pageable pageable);
    Page<Notification> findByUserAndIsRead(User user, Boolean isRead, Pageable pageable);
    Long countByUserAndIsReadFalse(User user);
}
