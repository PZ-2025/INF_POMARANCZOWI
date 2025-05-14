package com.orange.bookmanagment.reservation.web.mapper;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.web.model.ReservationDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.stereotype.Component;

/**
 * Odpowiada za mapowanie encji {@link Reservation}
 * na obiekt DTO {@link ReservationDto}, który jest używany w odpowiedziach API.
 */
@Component
public class ReservationDtoMapper {

    /**
     * Mapuje obiekt {@link Reservation} na {@link ReservationDto}.
     *
     * @param reservation obiekt rezerwacji do zmapowania
     * @return DTO z danymi rezerwacji
     */
    public ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getBookId(),
                reservation.getUserId(),
                reservation.getStatus(),
                reservation.getQueuePosition(),
                TimeUtil.getTimeInStandardFormat(reservation.getReservedAt()),
                TimeUtil.getTimeInStandardFormat(reservation.getExpiresAt())
        );
    }
}
