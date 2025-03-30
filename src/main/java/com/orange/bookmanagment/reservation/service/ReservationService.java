package com.orange.bookmanagment.reservation.service;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.user.model.User;

public interface ReservationService {

     Reservation createReservation(Book book, User user);

}
