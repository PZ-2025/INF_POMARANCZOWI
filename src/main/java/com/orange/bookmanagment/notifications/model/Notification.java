package com.orange.bookmanagment.notifications.model;


import com.orange.bookmanagment.notifications.model.enums.NotificationStatus;
import com.orange.bookmanagment.notifications.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity(name = "notification")
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long userId;

    private String title;

    private String body;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private Instant createdAt;

    private Instant updatedAt;

    public Notification(String title, String body, NotificationStatus notificationStatus, NotificationType notificationType, Instant createdAt, Instant updatedAt) {
        this.title = title;
        this.body = body;
        this.notificationStatus = notificationStatus;
        this.notificationType = notificationType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
