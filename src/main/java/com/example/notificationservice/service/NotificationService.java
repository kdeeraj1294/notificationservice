package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.exception.NotificationNotFoundException;
import com.example.notificationservice.provider.NotificationProvider;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationProvider provider;

    public NotificationService(
            NotificationRepository repository,
            NotificationProvider provider
    ) {
        this.repository = repository;
        this.provider = provider;
    }

    @Transactional
    public NotificationResponse create(NotificationRequest request) {

        Notification notification = new Notification(
                request.recipient(),
                request.message(),
                request.channel()
        );

        notification = repository.save(notification);

        try {
            notification.markProcessing();
            repository.save(notification);

            provider.send(notification);

            notification.markSent();
        } catch (Exception ex) {
            notification.markFailed();
            repository.save(notification);

            return toResponse(notification);
        }

        return toResponse(notification);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getById(UUID id) {

        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new NotificationNotFoundException(id)
                );

        return toResponse(notification);
    }

    private NotificationResponse toResponse(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getRecipient(),
                notification.getMessage(),
                notification.getChannel(),
                notification.getStatus(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
