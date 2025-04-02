package com.orange.bookmanagment.reservation.service;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.user.model.User;

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

}
