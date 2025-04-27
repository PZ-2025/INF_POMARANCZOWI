package com.orange.bookmanagment.shared.enums;

/**
 * Enum representing the status of a reservation.
 * <p>
 * The possible statuses are:
 * <ul>
 *     <li>PENDING: The book is reserved but not yet available (in queue).</li>
 *     <li>READY: The book is available and waiting for pickup by the user who reserved it.</li>
 *     <li>CANCELLED: The reservation was cancelled by the user.</li>
 *     <li>COMPLETED: The reservation was completed (user picked up the book).</li>
 *     <li>EXPIRED: The reservation expired (user didn't pick up the book in time).</li>
 * </ul>
 */
public enum ReservationStatus {
    PENDING,
    READY,
    CANCELLED,
    COMPLETED,
    EXPIRED,
}
