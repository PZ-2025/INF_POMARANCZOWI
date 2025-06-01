package com.orange.bookmanagment.user.api.dto;

/**
 * DTO reprezentujące użytkownika dla raportów PDF
 */
public record UserExternalDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {

    /**
     * Konstruktor konwertujący z encji User
     */
    public static UserExternalDto fromUser(com.orange.bookmanagment.user.model.User user) {
        return new UserExternalDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}