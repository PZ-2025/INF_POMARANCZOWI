package com.orange.bookmanagment.user.model;

import com.orange.bookmanagment.user.model.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * Główna encja reprezentująca użytkownika systemu zarządzania książkami.
 * <p>
 * Zawiera podstawowe dane użytkownika, statusy bezpieczeństwa oraz typ użytkownika (rola).
 */
@Entity
@Table(name = "users")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private boolean blocked;

    private boolean verified;

    private boolean locked;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant changedPasswordAt;

    private Instant blockedAt;

    private Instant verifiedAt;

    private Instant lockedAt;

    private String avatarPath;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userSettings", referencedColumnName = "id")
    private Settings userSettings;

    /**
     * Konstruktor tworzący nowego użytkownika z domyślnymi ustawieniami i czasami.
     *
     * @param password hasło użytkownika
     * @param email adres e-mail użytkownika
     * @param lastName nazwisko użytkownika
     * @param firstName imię użytkownika
     * @param userType typ użytkownika (rola)
     */
    public User(String password, String email, String lastName, String firstName, UserType userType) {
        this.password = password;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.userType = userType;

        //TODO add verification system
        this.verified = false;
        this.blocked = false;
        this.locked = false;

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.changedPasswordAt = Instant.now();
        this.blockedAt = Instant.now();
        this.verifiedAt = Instant.now();
        this.lockedAt = Instant.now();

        this.userSettings = new Settings(
                false,
                true,
                true,
                true,
                true
        );
    }

    /**
     * Konstruktor tworzący użytkownika z dodatkową ścieżką do avatara.
     *
     * @param password hasło
     * @param email adres e-mail
     * @param lastName nazwisko
     * @param firstName imię
     * @param userType typ użytkownika
     * @param avatarPath ścieżka do avatara
     */
    public User(String password, String email, String lastName, String firstName, UserType userType, String avatarPath) {
        this(password, email, lastName, firstName, userType);
        this.avatarPath = avatarPath;
    }

    /**
     * Zmienia hasło użytkownika i aktualizuje daty zmian.
     *
     * @param password nowe hasło
     */
    public void changePassword(String password) {
        this.password = password;
        this.changedPasswordAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
