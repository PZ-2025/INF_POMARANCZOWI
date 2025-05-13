package com.orange.bookmanagment.shared.enums;

/**
 * Enum reprezentujący status wypożyczenia książki.
 *
 * <p>Dostępne statusy:</p>
 * <ul>
 *   <li>{@code ACTIVE} – Wypożyczenie aktywne.</li>
 *   <li>{@code OVERDUE} – Wypożyczenie przeterminowane.</li>
 *   <li>{@code RETURNED} – Książka została zwrócona.</li>
 *   <li>{@code LOST} – Książka została zgubiona.</li>
 * </ul>
 */
public enum LoanStatus {
    ACTIVE,
    OVERDUE,
    RETURNED,
    LOST
}
