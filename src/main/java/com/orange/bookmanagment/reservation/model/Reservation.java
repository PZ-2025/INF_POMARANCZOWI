package com.orange.bookmanagment.reservation.model;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "reservations")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Instant reservedAt;
    private Instant expiresAt;
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private int queuePosition;

    public Reservation(Book book, User user, ReservationStatus status, int queuePosition) {
        this.book = book;
        this.user = user;
        this.status = status;
        this.queuePosition = queuePosition;
        this.reservedAt = Instant.now();
        this.expiresAt = reservedAt.plusSeconds(2592000); // 30 days
        this.updatedAt = reservedAt;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
