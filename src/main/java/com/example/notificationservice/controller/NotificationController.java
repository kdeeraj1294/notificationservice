package com.example.notificationservice.controller;
import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse create(
            @Valid @RequestBody NotificationRequest request
    ) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public NotificationResponse getById(
            @PathVariable UUID id
    ) {
        return service.getById(id);
    }
}