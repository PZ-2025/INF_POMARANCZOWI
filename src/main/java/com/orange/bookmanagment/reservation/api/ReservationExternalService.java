package com.orange.bookmanagment.reservation.api;


import com.orange.bookmanagment.reservation.api.dto.ReservationExternalDto;

public interface ReservationExternalService {

    boolean processReturnedBook(long bookId);

    boolean isBookReservedForUser(Long bookId, Long userId);

    ReservationExternalDto completeReservation(long bookId, long userId);

}
