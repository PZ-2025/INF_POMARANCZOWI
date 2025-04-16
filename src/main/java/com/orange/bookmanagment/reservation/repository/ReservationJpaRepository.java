package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.user.model.User;
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
     * Checks if a reservation exists for a given book and user with specified statuses.
     *
     * @param book       the book associated with the reservation
     * @param user       the user associated with the reservation
     * @param statusList the list of reservation statuses to check against
     * @return true if a reservation exists, false otherwise
     */
    boolean existsByBookAndUserAndStatusIn(Book book, User user, List<ReservationStatus> statusList);

    /**
     * Counts the number of reservations for a given book with specified statuses.
     *
     * @param book       the book associated with the reservations
     * @param statusList the list of reservation statuses to count against
     * @return the count of reservations matching the criteria
     */
    int countByBookAndStatusIn(Book book, List<ReservationStatus> statusList);

    /**
     * Finds the first reservation for a given book with a specified status, ordered by queue position.
     *
     * @param book   the book associated with the reservation
     * @param status the status of the reservation
     * @return an Optional containing the found reservation, or empty if not found
     */
    Optional<Reservation> findFirstByBookAndStatusOrderByQueuePosition(Book book, ReservationStatus status);

    /**
     * Finds all reservations for a given book with a specified status, ordered by queue position.
     *
     * @param book   the book associated with the reservations
     * @param status the status of the reservations
     * @return a list of reservations matching the criteria
     */
    List<Reservation> findByBookAndStatusOrderByQueuePosition(Book book, ReservationStatus status);

    /**
     * Finds a reservation for a given book and user with a specified status.
     *
     * @param book   the book associated with the reservation
     * @param user   the user associated with the reservation
     * @param status the status of the reservation
     * @return an Optional containing the found reservation, or empty if not found
     */
    Optional<Reservation> findByBookAndUserAndStatus(Book book, User user, ReservationStatus status);

    /**
     * Finds all reservations for a given user.
     *
     * @param user the user associated with the reservations
     * @return a list of reservations for the user
     */
    List<Reservation> findByUser(User user);

    /**
     * Finds all reservations for a given book.
     *
     * @param book the book associated with the reservations
     * @return a list of reservations for the book
     */
    List<Reservation> findByBook(Book book);

    /**
     * Finds all reservations for a given book with specified statuses, ordered by queue position.
     *
     * @param book       the book associated with the reservations
     * @param statusList the list of reservation statuses to filter by
     * @return a list of reservations matching the criteria
     */
    List<Reservation> findByBookAndStatusInOrderByQueuePosition(Book book, List<ReservationStatus> statusList);

    /**
     * Finds all reservations for a given user with specified statuses, ordered by queue position.
     *
     * @param user       the user associated with the reservations
     * @param statusList the list of reservation statuses to filter by
     * @return a list of reservations matching the criteria
     */
    List<Reservation> findByUserAndStatusInOrderByQueuePosition(User user, List<ReservationStatus> statusList);




}
