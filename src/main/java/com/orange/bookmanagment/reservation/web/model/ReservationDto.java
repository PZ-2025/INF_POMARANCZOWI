package com.orange.bookmanagment.reservation.web.model;

import com.orange.bookmanagment.shared.enums.ReservationStatus;

public record ReservationDto(
        Long id,
        Long bookId,
        Long userId,
        ReservationStatus status,
        int queuePosition,
        String reservedAt,
        String expiresAt
) { }
