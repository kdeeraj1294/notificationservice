package com.example.notificationservice.dto;

import com.example.notificationservice.entity.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(

        @NotBlank(message = "Recipient is required")
        String recipient,

        @NotBlank(message = "Message is required")
        String message,

        @NotNull(message = "Channel is required")
        NotificationChannel channel

) {
}
