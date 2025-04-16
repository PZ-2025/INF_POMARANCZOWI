package com.orange.bookmanagment.reservation.service;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.exception.ReservationNotFoundException;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.user.model.User;

import java.util.List;

/**
 * ReservationService is an interface that defines the contract for reservation-related operations.
 * It provides methods to create reservations and check the status of reservations.
 */
public interface ReservationService {

     /**
      * Creates a reservation for a book by a user.
      *
      * @param book the book to be reserved
      * @param user the user who wants to reserve the book
      * @return the created reservation
      * @throws BookAlreadyReservedException if the book is already reserved by the user
      */
     Reservation createReservation(Book book, User user) throws BookAlreadyReservedException;

     /**
      * <p>Cancel a reservation</p>
      *
      * @param reservation ID of the reservation to cancel
      * @return the updated reservation
      * @throws IllegalStateException if the reservation cannot be canceled
      */
     Reservation cancelReservation(Reservation reservation) throws IllegalStateException;

     /**
      * Completes a reservation for a book by a user.
      *
      * @param book the book to be reserved
      * @param user the user who wants to reserve the book
      * @return true if the reservation was completed successfully, false otherwise
      */
     boolean completeReservation(Book book, User user);

     /**
      * <p>Process returned book - check for pending reservations</p>
      *
      * @param book the returned book
      * @return true if there is an active reservation that is now ready for pickup
      */
     boolean processReturnedBook(Book book);

     /**
      * <p>Get all reservations for a user</p>
      *
      * @param user user to get reservations for
      * @return list of reservations for the user
      */
     List<Reservation> getUserReservations(User user);

     /**
     * <p>Get all active reservations for a user</p>
     *
     * @param user user to get active reservations for
     * @return list of active reservations for the user
     */
     List<Reservation> getActiveUserReservations(User user);

     /**
      * <p>Get all reservations for a book</p>
      *
      * @param book book to get reservations for
      * @return list of reservations for the book
      */
     List<Reservation> getBookReservations(Book book);

     /**
      * <p>Get active reservations for a book</p>
      *
      * @param book book to get active reservations for
      * @return list of active reservations for the book
      */
     List<Reservation> getActiveBookReservations(Book book);

     /**
      * <p>Check if book is reserved for user</p>
      *
      * @param book book to check
      * @param user user to check
      * @return true if the book is reserved for the user
      */
     boolean isBookReservedForUser(Book book, User user);

     /**
     * <p>Get reservation by ID</p>
     *
     * @param reservationId ID of the reservation to get
     * @return the reservation with the given ID
     * @throws ReservationNotFoundException if the reservation is not found
     */
     Reservation getReservationById(Long reservationId) throws ReservationNotFoundException;
}
