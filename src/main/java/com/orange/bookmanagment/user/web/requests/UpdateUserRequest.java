package com.orange.bookmanagment.user.web.requests;

import lombok.Data;

/**
 * Reprezentuje żądanie aktualizacji danych użytkownika.
 */
@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
}
