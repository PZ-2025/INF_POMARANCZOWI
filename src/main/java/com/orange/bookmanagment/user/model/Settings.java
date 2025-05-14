package com.orange.bookmanagment.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Reprezentuje ustawienia użytkownika w systemie.
 * <p>
 * Zawiera opcje związane z wyglądem interfejsu oraz preferencjami dotyczącymi powiadomień.
 */
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

    /**
     * Tworzy nowe ustawienia użytkownika na podstawie przekazanych parametrów.
     *
     * @param darkMode czy tryb ciemny jest włączony
     * @param notificationsEnabled czy powiadomienia są włączone
     * @param emailNotificationsEnabled czy powiadomienia e-mail są włączone
     * @param emailRemindersEnabled czy przypomnienia e-mail są włączone
     * @param newBooksNotificationsEnabled czy powiadomienia o nowych książkach są włączone
     */
    public Settings(boolean darkMode, boolean notificationsEnabled, boolean emailNotificationsEnabled, boolean emailRemindersEnabled, boolean newBooksNotificationsEnabled) {
        this.darkMode = darkMode;
        this.notificationsEnabled = notificationsEnabled;
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.emailRemindersEnabled = emailRemindersEnabled;
        this.newBooksNotificationsEnabled = newBooksNotificationsEnabled;
    }
}
