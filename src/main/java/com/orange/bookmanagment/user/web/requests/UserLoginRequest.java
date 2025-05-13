package com.orange.bookmanagment.user.web.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Żądanie logowania użytkownika.
 *
 * @param email adres e-mail użytkownika
 * @param password hasło użytkownika (min. 6 znaków)
 */
public record UserLoginRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        @Size(min = 6,max = 256)
        String password
) { }
