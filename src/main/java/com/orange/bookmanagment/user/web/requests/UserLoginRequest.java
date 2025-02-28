package com.orange.bookmanagment.user.web.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Login request with 2 parameters
 * @param email - User email
 * @param password - User password
 */
public record UserLoginRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        @Size(min = 6,max = 256)
        String password
) {
}
