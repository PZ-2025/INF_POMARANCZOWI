package com.orange.bookmanagment.user.model;


import com.orange.bookmanagment.user.model.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * <p>Main actor of book management system, representing standard user information</p>
 *
 * <p>UserType is represent as type of user in system</p>
 */

@Entity
@Table(name = "users")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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

    @Enumerated(EnumType.STRING)
    private UserType userType;

    /**
     * <p>Default constructor to create a new user with default values for Instant type variables</p>
     * @param password - User password
     * @param email - User email
     * @param lastName - User first name
     * @param firstName - User last name
     * @param userType - Type of user
     */
    public User(String password, String email, String lastName, String firstName, UserType userType) {
        this.password = password;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.userType = userType;

        //TODO add verification system
        this.verified = true;
        this.blocked = false;
        this.locked = false;

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.changedPasswordAt = Instant.now();
        this.blockedAt = Instant.now();
        this.verifiedAt = Instant.now();
        this.lockedAt = Instant.now();
    }

    public void changePassword(String password) {
        this.password = password;
        this.changedPasswordAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
