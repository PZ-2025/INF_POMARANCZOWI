package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.shared.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ReservationJpaRepository is a Spring Data JPA repository interface for managing Reservation entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation,Long> {

    /**
     * Checks if a reservation exists for a specific book and user with the given statuses.
     *
     * @param bookId     the ID of the book
     * @param userId     the ID of the user
     * @param statusList the list of reservation statuses to check
     * @return true if a reservation exists, false otherwise
     */
    boolean existsByBookIdAndUserIdAndStatusIn(Long bookId, Long userId, List<ReservationStatus> statusList);

    /**
     * Counts the number of reservations for a specific book with the given statuses.
     *
     * @param bookId     the ID of the book
     * @param statusList the list of reservation statuses to count
     * @return the number of reservations matching the criteria
     */
    int countByBookIdAndStatusIn(Long bookId, List<ReservationStatus> statusList);

    /**
     * Finds a reservation by its ID.
     *
     * @param id the ID of the reservation
     * @return an Optional containing the found reservation, or empty if not found
     */
    Optional<Reservation> findFirstByBookIdAndStatusOrderByQueuePosition(Long bookId, ReservationStatus status);

    /**
     * Finds all reservations for a given book with a specified status, ordered by queue position.
     *
     * @param bookId the ID of the book
     * @param status the status of the reservations
     * @return a list of reservations matching the criteria
     */
    List<Reservation> findByBookIdAndStatusOrderByQueuePosition(Long bookId, ReservationStatus status);

    /**
     * Finds a reservation by book, user, and status.
     *
     * @param bookId   the ID of the book
     * @param userId   the ID of the user
     * @param status   the status of the reservation
     * @return an Optional containing the found reservation, or empty if not found
     */
    Optional<Reservation> findByBookIdAndUserIdAndStatus(Long bookId, Long userId, ReservationStatus status);

    /**
     * Finds all reservations for a given user.
     *
     * @param user the user associated with the reservations
     * @return a list of reservations for the user
     */
    List<Reservation> findByUserId(Long userId);

    /**
     * Finds all reservations for a given book.
     *
        * @param bookId the ID of the book associated with the reservations
     * @return a list of reservations for the book
     */
    List<Reservation> findByBookId(Long bookId);

    /**
     * Finds all reservations for a given book with specified statuses, ordered by queue position.
     *
        * @param bookId    the ID of the book associated with the reservations
     * @param statusList the list of reservation statuses to filter by
     * @return a list of reservations matching the criteria
     */
    List<Reservation> findByBookIdAndStatusInOrderByQueuePosition(Long bookId, List<ReservationStatus> statusList);

    /**
     * Finds all reservations for a given user with specified statuses, ordered by queue position.
     *
        * @param userId     the ID of the user associated with the reservations
     * @param statusList the list of reservation statuses to filter by
     * @return a list of reservations matching the criteria
     */
    List<Reservation> findByUserIdAndStatusInOrderByQueuePosition(Long userId, List<ReservationStatus> statusList);

    boolean existsByBookIdAndUserIdNotAndStatusIn(Long bookId, Long userId, List<ReservationStatus> statusList);

    Optional<Reservation> findFirstByBookIdAndStatusOrderByQueuePosition(long bookId, ReservationStatus status);
}
