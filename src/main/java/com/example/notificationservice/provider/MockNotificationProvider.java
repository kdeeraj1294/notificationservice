package com.example.notificationservice.provider;

import com.example.notificationservice.entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockNotificationProvider implements NotificationProvider {

    private static final Logger log =
            LoggerFactory.getLogger(MockNotificationProvider.class);

    @Override
    public void send(Notification notification) {

        log.info(
                "Sending {} notification to {}",
                notification.getChannel(),
                notification.getRecipient()
        );

        if ("fail@test.com".equalsIgnoreCase(notification.getRecipient())) {
            throw new IllegalStateException("Mock provider failed");
        }
    }
}
