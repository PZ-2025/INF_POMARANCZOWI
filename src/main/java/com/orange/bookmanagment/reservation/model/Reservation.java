package com.orange.bookmanagment.reservation.model;

import com.orange.bookmanagment.shared.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents a reservation of a book by a user.
 * <p>
 * This entity contains information about the book being reserved, the user who reserved it,
 * the status of the reservation, and the queue position of the reservation.
 */
@Entity
@Table(name = "reservations")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id", nullable = false)
//    private Book book;
    private long bookId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
    private long userId;

    private Instant reservedAt;
    private Instant expiresAt;
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private int queuePosition;

    /**
     * Constructs a new Reservation object with the specified parameters.
     *
     * @param bookId       The ID of the book being reserved.
     * @param userId       The ID of the user who reserved the book.
     * @param status       The status of the reservation.
     * @param queuePosition The queue position of the reservation.
     */
    public Reservation(long bookId, long userId, ReservationStatus status, int queuePosition) {
        this.bookId = bookId;
        this.userId = userId;
        this.status = status;
        this.queuePosition = queuePosition;
        this.reservedAt = Instant.now();
        this.expiresAt = reservedAt.plusSeconds(2592000); // 30 days
        this.updatedAt = reservedAt;
    }

    /**
     * Updates the status of the reservation.
     *
     * @param status The new status of the reservation.
     */
    public void setStatus(ReservationStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the queue position of the reservation.
     *
     * @param queuePosition The new queue position of the reservation.
     */
    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
        this.updatedAt = Instant.now();
    }

}
