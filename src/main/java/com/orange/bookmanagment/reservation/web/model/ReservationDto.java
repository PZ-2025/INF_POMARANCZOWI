package com.orange.bookmanagment.reservation.web.model;

import com.orange.bookmanagment.shared.enums.ReservationStatus;

public record ReservationDto(
        Long bookId,
        Long userId,
        String reservedAt,
        String expiresAt,
        ReservationStatus status,
        int queuePosition
) {
}
