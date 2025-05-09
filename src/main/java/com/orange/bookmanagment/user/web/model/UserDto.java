package com.orange.bookmanagment.user.web.model;

public record UserDto (
        Long id,
        String firstName,
        String lastName,
        String email,
        String userType,
        String avatarPath,
        boolean verified,
        boolean blocked,
        String createdAt,
        String updatedAt,
        String blockedAt,
        String verifiedAt
) { }
