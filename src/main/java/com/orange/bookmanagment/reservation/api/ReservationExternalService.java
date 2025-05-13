package com.orange.bookmanagment.reservation.api;

import com.orange.bookmanagment.reservation.api.dto.ReservationExternalDto;

import java.util.List;

/**
 * Interfejs do operacji zewnętrznych na rezerwacjach książek.
 * <p>
 * Udostępnia metody do sprawdzania i modyfikowania stanu rezerwacji,
 * obsługi kolejki rezerwacyjnej oraz oznaczania książek jako gotowych do odbioru.
 */
public interface ReservationExternalService {

    /**
     * Przetwarza zwróconą książkę i obsługuje odpowiednie rezerwacje.
     *
     * @param bookId ID zwróconej książki
     * @return true, jeśli rezerwacja została przetworzona
     */
    boolean processReturnedBook(long bookId);

    /**
     * Sprawdza, czy książka jest zarezerwowana przez danego użytkownika.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @return true, jeśli książka jest zarezerwowana przez użytkownika
     */
    boolean isBookReservedForUser(Long bookId, Long userId);

    /**
     * Zamyka rezerwację i oznacza ją jako zakończoną (zrealizowaną).
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @return DTO z informacjami o zakończonej rezerwacji
     */
    ReservationExternalDto completeReservation(long bookId, long userId);

    /**
     * Zwraca listę oczekujących rezerwacji dla danej książki.
     *
     * @param bookId ID książki
     * @return lista oczekujących rezerwacji
     */
    List<ReservationExternalDto> getPendingReservations(long bookId);

    /**
     * Oznacza rezerwację jako gotową do odbioru (status READY).
     *
     * @param reservationId ID rezerwacji
     */
    void markAsReady(long reservationId);

    /**
     * Zmniejsza pozycję w kolejce dla danej rezerwacji.
     *
     * @param reservationId ID rezerwacji
     */
    void decrementQueuePosition(long reservationId);

    /**
     * Sprawdza, czy książka jest zarezerwowana przez innego użytkownika niż podany.
     *
     * @param bookId ID książki
     * @param userId ID aktualnego użytkownika
     * @return true, jeśli zarezerwowana jest przez innego użytkownika
     */
    boolean isReservedByAnotherUser(long bookId, long userId);

    /**
     * Alias do metody {@link #isReservedByAnotherUser}.
     *
     * @param bookId ID książki
     * @param currentUserId ID bieżącego użytkownika
     * @return true, jeśli zarezerwowana przez innego użytkownika
     */
    boolean isBookReservedForAnotherUser(Long bookId, Long currentUserId);
}
