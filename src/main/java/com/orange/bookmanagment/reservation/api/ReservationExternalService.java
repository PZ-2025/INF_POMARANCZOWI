package com.orange.bookmanagment.reservation.api;

import com.orange.bookmanagment.reservation.api.dto.ReservationInternalDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReservationExternalService {

    boolean processReturnedBook(long bookId);

    boolean isBookReservedForUser(Long bookId, Long userId);

    void completeReservation(long bookId, long userId);

//    List<ReservationInternalDto> getActiveBookReservations(long bookId);
//    ReservationInternalDto createReservationExternal(long bookId, long userId);
}
