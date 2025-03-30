package com.orange.bookmanagment.reservation.model.enums;

public enum ReservationStatus {
    /**
     * Book is reserved but not yet available (in queue)
     */
    PENDING,

    /**
     * Book is available and waiting for pickup by the user who reserved it
     */
    READY,

    /**
     * Reservation was cancelled by user
     */
    CANCELLED,

    /**
     * Reservation was completed (user picked up the book)
     */
    COMPLETED,

    /**
     * Reservation expired (user didn't pick up the book in time)
     */
    EXPIRED,

}
