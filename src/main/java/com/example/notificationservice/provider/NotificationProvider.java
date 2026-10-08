package com.example.notificationservice.provider;

import com.example.notificationservice.entity.Notification;

public interface NotificationProvider {

    void send(Notification notification);
}
