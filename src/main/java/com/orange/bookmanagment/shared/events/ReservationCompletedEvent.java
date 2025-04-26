package com.orange.bookmanagment.shared.events;

public record ReservationCompletedEvent(Long bookId, Long userId) {
}

