package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.NotificationChannel;
import com.example.notificationservice.entity.NotificationStatus;
import com.example.notificationservice.exception.GlobalExceptionHandler;
import com.example.notificationservice.exception.NotificationNotFoundException;
import com.example.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(GlobalExceptionHandler.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService service;

    @Test
    void shouldCreateNotification() throws Exception {

        UUID id = UUID.randomUUID();

        NotificationResponse response = new NotificationResponse(
                id,
                "test@gmail.com",
                "Test notification",
                NotificationChannel.EMAIL,
                NotificationStatus.SENT,
                Instant.now(),
                Instant.now()
        );

        when(service.create(any()))
                .thenReturn(response);

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipient": "test@gmail.com",
                                  "message": "Test notification",
                                  "channel": "EMAIL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.recipient").value("test@gmail.com"))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void shouldGetNotification() throws Exception {

        UUID id = UUID.randomUUID();

        NotificationResponse response = new NotificationResponse(
                id,
                "test@gmail.com",
                "Test notification",
                NotificationChannel.EMAIL,
                NotificationStatus.SENT,
                Instant.now(),
                Instant.now()
        );

        when(service.getById(eq(id)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/notifications/{id}", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.recipient").value("test@gmail.com"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipient": "",
                                  "message": "",
                                  "channel": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldReturn404WhenNotificationDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.getById(id))
                .thenThrow(new NotificationNotFoundException(id));

        mockMvc.perform(
                        get("/notifications/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Notification not found: " + id));
    }
}
