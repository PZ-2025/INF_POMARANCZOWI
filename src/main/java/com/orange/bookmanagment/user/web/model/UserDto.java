package com.orange.bookmanagment.user.web.model;


public record UserDto(
        String firstName,
        String lastName,
        String email,
        boolean verified,
        boolean blocked,
        String createdAt,
        String updatedAt,
        String blockedAt,
        String verifiedAt
) {
}
