package com.orange.bookmanagment.reservation.model;

import com.orange.bookmanagment.shared.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Reprezentuje rezerwację książki przez użytkownika.
 * <p>
 * Zawiera informacje o książce, użytkowniku, statusie rezerwacji, pozycji w kolejce
 * oraz terminie ważności rezerwacji.
 */
@Entity
@Table(name = "reservations")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
     * Tworzy nową rezerwację książki.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @param status status rezerwacji
     * @param queuePosition pozycja w kolejce
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
     * Aktualizuje status rezerwacji i ustawia datę aktualizacji.
     *
     * @param status nowy status rezerwacji
     */
    public void setStatus(ReservationStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    /**
     * Ustawia nową pozycję w kolejce i aktualizuje datę modyfikacji.
     *
     * @param queuePosition nowa pozycja w kolejce
     */
    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
        this.updatedAt = Instant.now();
    }
}
