package com.orange.bookmanagment.reservation.api.dto;

import com.orange.bookmanagment.shared.enums.ReservationStatus;

import java.time.Instant;

public record ReservationExternalDto(
        Long id,
        Long bookId,
        Long userId,
        ReservationStatus status,
        int queuePosition,
        Instant reservedAt,
        Instant expiresAt,
        Instant updatedAt
) {}
