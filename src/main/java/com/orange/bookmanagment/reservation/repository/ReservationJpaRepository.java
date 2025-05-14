package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.shared.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfejs JPA dla zarządzania encjami {@link Reservation}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD oraz definiowanie zapytań niestandardowych
 * na bazie konwencji nazw metod Spring Data JPA.
 */
@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation,Long> {

    /**
     * Sprawdza, czy istnieje rezerwacja użytkownika dla danej książki o określonych statusach.
     *
     * @param bookId identyfikator książki
     * @param userId identyfikator użytkownika
     * @param statusList lista statusów rezerwacji
     * @return true, jeśli istnieje pasująca rezerwacja
     */
    boolean existsByBookIdAndUserIdAndStatusIn(Long bookId, Long userId, List<ReservationStatus> statusList);

    /**
     * Zlicza rezerwacje dla danej książki o określonych statusach.
     *
     * @param bookId identyfikator książki
     * @param statusList lista statusów rezerwacji
     * @return liczba znalezionych rezerwacji
     */
    int countByBookIdAndStatusIn(Long bookId, List<ReservationStatus> statusList);

    /**
     * Czy dla danej książki istnieje jakakolwiek rezerwacja o jednym z podanych statusów.
     *
     * @param bookId ID książki
     * @param statusList lista statusów do sprawdzenia
     * @return true, jeśli istnieje co najmniej jedna taka rezerwacja
     */
    boolean existsByBookIdAndStatusIn(long bookId, List<ReservationStatus> statusList);

    /**
     * Zwraca pierwszą rezerwację dla książki o podanym statusie, posortowaną według pozycji w kolejce.
     *
     * @param bookId identyfikator książki
     * @param status status rezerwacji
     * @return opcjonalna rezerwacja
     */
    Optional<Reservation> findFirstByBookIdAndStatusOrderByQueuePosition(Long bookId, ReservationStatus status);

    /**
     * Zwraca wszystkie rezerwacje danej książki o wskazanym statusie, uporządkowane według pozycji w kolejce.
     *
     * @param bookId identyfikator książki
     * @param status status rezerwacji
     * @return lista rezerwacji
     */
    List<Reservation> findByBookIdAndStatusOrderByQueuePosition(Long bookId, ReservationStatus status);

    /**
     * Zwraca rezerwację użytkownika dla określonej książki i statusu.
     *
     * @param bookId identyfikator książki
     * @param userId identyfikator użytkownika
     * @param status status rezerwacji
     * @return opcjonalna rezerwacja
     */
    Optional<Reservation> findByBookIdAndUserIdAndStatus(Long bookId, Long userId, ReservationStatus status);

    /**
     * Zwraca wszystkie rezerwacje użytkownika.
     *
     * @param userId identyfikator użytkownika
     * @return lista rezerwacji
     */
    List<Reservation> findByUserId(Long userId);

    /**
     * Zwraca wszystkie rezerwacje dla danej książki.
     *
     * @param bookId identyfikator książki
     * @return lista rezerwacji
     */
    List<Reservation> findByBookId(Long bookId);

    /**
     * Zwraca wszystkie rezerwacje książki o określonych statusach, posortowane według pozycji w kolejce.
     *
     * @param bookId identyfikator książki
     * @param statusList lista statusów rezerwacji
     * @return lista rezerwacji
     */
    List<Reservation> findByBookIdAndStatusInOrderByQueuePosition(Long bookId, List<ReservationStatus> statusList);

    /**
     * Zwraca wszystkie rezerwacje użytkownika o określonych statusach, posortowane według pozycji w kolejce.
     *
     * @param userId identyfikator użytkownika
     * @param statusList lista statusów rezerwacji
     * @return lista rezerwacji
     */
    List<Reservation> findByUserIdAndStatusInOrderByQueuePosition(Long userId, List<ReservationStatus> statusList);

    /**
     * Sprawdza, czy książka jest zarezerwowana przez innego użytkownika niż wskazany, o podanych statusach.
     *
     * @param bookId identyfikator książki
     * @param userId identyfikator użytkownika do wykluczenia
     * @param statusList lista statusów rezerwacji
     * @return true, jeśli istnieje rezerwacja innego użytkownika
     */
    boolean existsByBookIdAndUserIdNotAndStatusIn(Long bookId, Long userId, List<ReservationStatus> statusList);

    /**
     * Alias metody {@link #findFirstByBookIdAndStatusOrderByQueuePosition(Long, ReservationStatus)} ze zmienioną sygnaturą.
     *
     * @param bookId identyfikator książki
     * @param status status rezerwacji
     * @return opcjonalna rezerwacja
     */
    Optional<Reservation> findFirstByBookIdAndStatusOrderByQueuePosition(long bookId, ReservationStatus status);
}
