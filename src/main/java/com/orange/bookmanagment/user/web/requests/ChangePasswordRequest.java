package com.orange.bookmanagment.user.web.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Rekord reprezentujący żądanie zmiany hasła użytkownika.
 * Zawiera stare i nowe hasło, które podlegają walidacji pod kątem długości oraz braku pustych wartości.
 *
 * @param oldPassword dotychczasowe hasło użytkownika
 * @param newPassword nowe hasło użytkownika
 */
public record ChangePasswordRequest(
        @NotBlank
        @Size(min = 6,max = 256)
        String oldPassword,
        @NotBlank
        @Size(min = 6,max = 256)
        String newPassword
) { }
