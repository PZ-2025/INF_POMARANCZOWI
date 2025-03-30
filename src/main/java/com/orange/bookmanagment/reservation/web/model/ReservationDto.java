package com.orange.bookmanagment.reservation.web.model;

import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;

public record ReservationDto(
        Long bookId,
        Long userId,
        String reservedAt,
        String expiresAt,
        ReservationStatus status,
        int queuePosition
) {
}
