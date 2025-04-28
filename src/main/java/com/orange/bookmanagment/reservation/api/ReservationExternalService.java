package com.orange.bookmanagment.reservation.api;


import com.orange.bookmanagment.reservation.api.dto.ReservationInternalDto;

public interface ReservationExternalService {

    boolean processReturnedBook(long bookId);

    boolean isBookReservedForUser(Long bookId, Long userId);

    ReservationInternalDto completeReservation(long bookId, long userId);

}
