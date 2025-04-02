package com.orange.bookmanagment.reservation.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
