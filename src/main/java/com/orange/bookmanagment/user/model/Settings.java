package com.orange.bookmanagment.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "userSettings")
@Table(name = "userSettings")

@NoArgsConstructor
@Data
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean darkMode;

    private boolean notificationsEnabled;

    private boolean emailNotificationsEnabled;

    private boolean emailRemindersEnabled;

    private boolean newBooksNotificationsEnabled;

    @OneToOne(mappedBy = "userSettings")
    private User user;

    public Settings(boolean darkMode, boolean notificationsEnabled, boolean emailNotificationsEnabled, boolean emailRemindersEnabled, boolean newBooksNotificationsEnabled) {
        this.darkMode = darkMode;
        this.notificationsEnabled = notificationsEnabled;
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.emailRemindersEnabled = emailRemindersEnabled;
        this.newBooksNotificationsEnabled = newBooksNotificationsEnabled;
    }
}
