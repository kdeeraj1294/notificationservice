package com.example.notificationservice.dto;

import com.example.notificationservice.entity.NotificationChannel;
import com.example.notificationservice.entity.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String recipient,
        String message,
        NotificationChannel channel,
        NotificationStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
