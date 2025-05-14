package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.shared.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Klasa repozytorium pośrednicząca w operacjach na encjach {@link Reservation}.
 * <p>
 * Udostępnia metody do zapisu, pobierania, filtrowania i sprawdzania rezerwacji książek.
 */
@Repository
@AllArgsConstructor
public class ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    /**
     * Zapisuje rezerwację w bazie danych.
     *
     * @param reservation rezerwacja do zapisania
     * @return zapisana rezerwacja
     */
    public Reservation saveReservation(Reservation reservation){
        return reservationJpaRepository.save(reservation);
    }

    /**
     * Sprawdza, czy istnieje rezerwacja użytkownika dla danej książki o jednym z podanych statusów.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @param statuses lista statusów
     * @return true, jeśli istnieje taka rezerwacja
     */
    public boolean existsByBookIdAndUserIdAndStatusIn(long bookId, long userId, List<ReservationStatus> statuses) {
        return reservationJpaRepository.existsByBookIdAndUserIdAndStatusIn(bookId, userId, statuses);
    }

    /**
     * Zlicza rezerwacje książki o podanych statusach.
     *
     * @param bookId ID książki
     * @param statusList lista statusów
     * @return liczba rezerwacji
     */
    public int countByBookIdAndStatusIn(long bookId, List<ReservationStatus> statusList) {
        return reservationJpaRepository.countByBookIdAndStatusIn(bookId, statusList);
    }

    /**
     * Sprawdza, czy książka ma przynajmniej jedną rezerwację o podanym statusie.
     *
     * @param bookId ID książki
     * @param statusList lista statusów
     * @return true, jeśli istnieje co najmniej jedna rezerwacja
     */
    public boolean existsByBookIdAndStatusIn(long bookId, List<ReservationStatus> statusList) {
        return reservationJpaRepository.existsByBookIdAndStatusIn(bookId, statusList);
    }

    /**
     * Pobiera rezerwację po ID.
     *
     * @param id identyfikator rezerwacji
     * @return opcjonalna rezerwacja
     */
    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    /**
     * Zwraca pierwszą rezerwację książki o danym statusie według pozycji w kolejce.
     *
     * @param bookId ID książki
     * @param status status rezerwacji
     * @return opcjonalna rezerwacja
     */
    public Optional<Reservation> findFirstByBookIdAndStatusOrderByQueuePosition(long bookId, ReservationStatus status) {
        return reservationJpaRepository.findFirstByBookIdAndStatusOrderByQueuePosition(bookId, status);
    }

    /**
     * Zwraca wszystkie rezerwacje książki o danym statusie posortowane według pozycji w kolejce.
     *
     * @param bookId ID książki
     * @param status status rezerwacji
     * @return lista rezerwacji
     */
    public List<Reservation> findByBookIdAndStatusOrderByQueuePosition(long bookId, ReservationStatus status) {
        return reservationJpaRepository.findByBookIdAndStatusOrderByQueuePosition(bookId, status);
    }

    /**
     * Zwraca rezerwację użytkownika dla książki o konkretnym statusie.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @param status status rezerwacji
     * @return opcjonalna rezerwacja
     */
    public Optional<Reservation> findByBookIdAndUserIdAndStatus(long bookId, long userId, ReservationStatus status) {
        return reservationJpaRepository.findByBookIdAndUserIdAndStatus(bookId, userId, status);
    }

    /**
     * Zwraca wszystkie rezerwacje użytkownika.
     *
     * @param userId ID użytkownika
     * @return lista rezerwacji
     */
    public List<Reservation> findByUserId(long userId) {
        return reservationJpaRepository.findByUserId(userId);
    }

    /**
     * Zwraca wszystkie rezerwacje dla danej książki.
     *
     * @param bookId ID książki
     * @return lista rezerwacji
     */
    public List<Reservation> findByBookId(long bookId) {
        return reservationJpaRepository.findByBookId(bookId);
    }

    /**
     * Zwraca rezerwacje książki o określonych statusach, posortowane według pozycji w kolejce.
     *
     * @param bookId ID książki
     * @param statusList lista statusów
     * @return lista rezerwacji
     */
    public List<Reservation> findByBookIdAndStatusInOrderByQueuePosition(long bookId, List<ReservationStatus> statusList) {
        return reservationJpaRepository.findByBookIdAndStatusInOrderByQueuePosition(bookId, statusList);
    }

    /**
     * Zwraca rezerwacje użytkownika o określonych statusach, posortowane według pozycji w kolejce.
     *
     * @param userId ID użytkownika
     * @param statusList lista statusów
     * @return lista rezerwacji
     */
    public List<Reservation> findByUserIdAndStatusInOrderByQueuePosition(Long userId, List<ReservationStatus> statusList) {
        return reservationJpaRepository.findByUserIdAndStatusInOrderByQueuePosition(userId, statusList);
    }

    /**
     * Sprawdza, czy książka jest zarezerwowana przez kogoś innego niż dany użytkownik.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika do wykluczenia
     * @param statuses lista statusów
     * @return true, jeśli istnieje taka rezerwacja
     */
    public boolean existsByBookIdAndUserIdNotAndStatusIn(long bookId, long userId, List<ReservationStatus> statuses) {
        return reservationJpaRepository.existsByBookIdAndUserIdNotAndStatusIn(bookId, userId, statuses);
    }
}
