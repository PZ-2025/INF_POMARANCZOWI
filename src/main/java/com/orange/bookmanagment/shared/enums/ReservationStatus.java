package com.orange.bookmanagment.shared.enums;

/**
 * Enum reprezentujący status rezerwacji książki.
 *
 * <p>Dostępne statusy:</p>
 * <ul>
 *   <li>{@code PENDING} – Książka zarezerwowana, ale jeszcze niedostępna do odbioru (w kolejce).</li>
 *   <li>{@code READY} – Książka dostępna, gotowa do odbioru.</li>
 *   <li>{@code CANCELLED} – Rezerwacja anulowana.</li>
 *   <li>{@code COMPLETED} – Rezerwacja zakończona (książka odebrana).</li>
 *   <li>{@code EXPIRED} – Rezerwacja wygasła (brak odbioru w terminie).</li>
 * </ul>
 */
public enum ReservationStatus {
    PENDING,
    READY,
    CANCELLED,
    COMPLETED,
    EXPIRED,
}
