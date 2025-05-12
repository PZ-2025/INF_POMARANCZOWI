package com.orange.bookmanagment.reservation.web.mapper;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.web.model.ReservationDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.stereotype.Component;

/**
 * ReservationDtoMapper is responsible for mapping Reservation entities to ReservationDto objects.
 */
@Component
public class ReservationDtoMapper {
    /**
     * Maps a Reservation entity to a ReservationDto object.
     *
     * @param reservation the Reservation entity to map
     * @return the mapped ReservationDto object
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
