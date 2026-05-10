package edu.cit.mahinay.kwikq.features.notifications.service;

import edu.cit.mahinay.kwikq.features.notifications.entity.Notification;
import edu.cit.mahinay.kwikq.features.users.entity.User;
import edu.cit.mahinay.kwikq.features.notifications.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(User user, String title, String message, String type) {
        Notification notification = new Notification(user, title, message, type);
        return notificationRepository.save(notification);
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUser(user, pageable);
    }

    public Page<Notification> getUnreadNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserAndIsRead(user, false, pageable);
    }

    public Long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}
