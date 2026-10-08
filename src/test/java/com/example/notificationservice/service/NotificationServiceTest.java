package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.entity.NotificationChannel;
import com.example.notificationservice.entity.NotificationStatus;
import com.example.notificationservice.exception.NotificationNotFoundException;
import com.example.notificationservice.provider.NotificationProvider;
import com.example.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationProvider provider;

    @InjectMocks
    private NotificationService service;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification(
                "test@gmail.com",
                "Test notification",
                NotificationChannel.EMAIL
        );
    }

    @Test
    void shouldSendNotificationSuccessfully() {

        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.create(
                new NotificationRequest(
                        "test@gmail.com",
                        "Test notification",
                        NotificationChannel.EMAIL
                )
        );

        verify(provider).send(any(Notification.class));
        verify(repository, atLeast(2))
                .save(any(Notification.class));
    }

    @Test
    void shouldMarkNotificationAsFailedWhenProviderFails() {

        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new RuntimeException("Provider unavailable"))
                .when(provider)
                .send(any(Notification.class));

        NotificationResponse response = service.create(
                new NotificationRequest(
                        "test@gmail.com",
                        "Test notification",
                        NotificationChannel.EMAIL
                )
        );

        assertEquals(NotificationStatus.FAILED, response.status());

        verify(provider).send(any(Notification.class));
    }

    @Test
    void shouldReturnNotificationById() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.of(notification));

        NotificationResponse response = service.getById(id);

        assertNotNull(response);
        assertEquals("test@gmail.com", response.recipient());
        assertEquals(NotificationChannel.EMAIL, response.channel());

        verify(repository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenNotificationDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                NotificationNotFoundException.class,
                () -> service.getById(id)
        );

        verify(repository).findById(id);
    }
}
