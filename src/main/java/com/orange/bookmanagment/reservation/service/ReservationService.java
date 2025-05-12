package com.orange.bookmanagment.reservation.service;

import com.orange.bookmanagment.reservation.model.Reservation;

import java.util.List;

/**
 * ReservationService is an interface that defines the contract for reservation-related operations.
 * It provides methods to create reservations and check the status of reservations.
 */
public interface ReservationService {

     Reservation createReservation(long bookId, long userId);

     Reservation getReservationById(Long reservationId);

     Reservation cancelReservation(Reservation reservation);

     List<Reservation> getBookReservations(long bookId);

     List<Reservation> getActiveBookReservations(long bookId);

     List<Reservation> getUserReservations(long userId);

     List<Reservation> getActiveUserReservations(long userId);

     Reservation expireReservation(long reservationId);

     Reservation completeReservation(long reservationId);

     Reservation extendReservation(long reservationId);
}
