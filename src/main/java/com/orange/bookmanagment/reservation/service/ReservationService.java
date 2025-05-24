package com.orange.bookmanagment.reservation.service;

import com.orange.bookmanagment.reservation.api.dto.ReservationExternalDto;
import com.orange.bookmanagment.reservation.model.Reservation;

import java.util.List;

/**
 * Interfejs definiujący operacje związane z rezerwacjami książek.
 * Obejmuje tworzenie, anulowanie, przedłużanie, wygaszanie oraz pobieranie rezerwacji.
 */
public interface ReservationService {

     /**
      * Tworzy nową rezerwację dla książki przez użytkownika.
      *
      * @param bookId ID książki
      * @param userId ID użytkownika
      * @return utworzona rezerwacja
      */
     Reservation createReservation(long bookId, long userId);

     /**
      * Zwraca rezerwację na podstawie jej ID.
      *
      * @param reservationId ID rezerwacji
      * @return rezerwacja, jeśli istnieje
      */
     Reservation getReservationById(Long reservationId);

     /**
      * Anuluje aktywną rezerwację.
      *
      * @param reservation rezerwacja do anulowania
      * @return zaktualizowana rezerwacja z ustawionym statusem CANCELLED
      */
     Reservation cancelReservation(Reservation reservation);

     /**
      * Zwraca wszystkie rezerwacje dla danej książki.
      *
      * @param bookId ID książki
      * @return lista rezerwacji tej książki
      */
     List<Reservation> getBookReservations(long bookId);

     /**
      * Zwraca aktywne rezerwacje (PENDING lub READY) dla danej książki.
      *
      * @param bookId ID książki
      * @return lista aktywnych rezerwacji książki
      */
     List<Reservation> getActiveBookReservations(long bookId);

     /**
      * Zwraca wszystkie rezerwacje użytkownika.
      *
      * @param userId ID użytkownika
      * @return lista rezerwacji użytkownika
      */
     List<Reservation> getUserReservations(long userId);

     /**
      * Zwraca aktywne rezerwacje użytkownika (PENDING lub READY).
      *
      * @param userId ID użytkownika
      * @return lista aktywnych rezerwacji użytkownika
      */
     List<Reservation> getActiveUserReservations(long userId);

     /**
      * Wygasza rezerwację (zmienia status na EXPIRED).
      *
      * @param reservationId ID rezerwacji do wygaszenia
      * @return zaktualizowana rezerwacja
      */
     Reservation expireReservation(long reservationId);

     /**
      * Oznacza rezerwację jako zrealizowaną (COMPLETED) i tworzy wypożyczenie.
      *
      * @param reservationId ID rezerwacji
      * @return zaktualizowana rezerwacja
      */
     Reservation completeReservation(long reservationId);

     /**
      * Przedłuża ważność aktywnej rezerwacji typu READY.
      *
      * @param reservationId ID rezerwacji do przedłużenia
      * @return zaktualizowana rezerwacja
      */
     Reservation extendReservation(long reservationId);

     /**
      * Zwraca aktywne rezerwacje (PENDING lub READY) książki na potrzeby oznaczenia jako zgubiona.
      *
      * @param bookId ID książki
      * @return lista aktywnych rezerwacji
      */
     List<ReservationExternalDto> getActiveBookReservationsForMark(long bookId);

     /**
      * Anuluje rezerwację na potrzeby oznaczenia książki jako zgubionej.
      *
      * @param reservationId ID rezerwacji
      */
     void cancelReservationForMark(Long reservationId);

    int getQueueLength(long bookId);
}
