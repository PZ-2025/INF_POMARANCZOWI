package com.orange.bookmanagment.reservation.api;

import com.orange.bookmanagment.reservation.api.dto.ReservationExternalDto;
import java.util.List;

public interface ReservationExternalService {

    boolean processReturnedBook(long bookId);

    boolean isBookReservedForUser(Long bookId, Long userId);

    ReservationExternalDto completeReservation(long bookId, long userId);

    List<ReservationExternalDto> getPendingReservations(long bookId);

    void markAsReady(long reservationId);

    void decrementQueuePosition(long reservationId);

    boolean isReservedByAnotherUser(long bookId, long userId);

    boolean isBookReservedForAnotherUser(Long bookId, Long currentUserId);
}
