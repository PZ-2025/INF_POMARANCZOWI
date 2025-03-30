package com.orange.bookmanagment.reservation.web.mapper;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.web.model.ReservationDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.stereotype.Component;

@Component
public class ReservationDtoMapper {
    public ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getBook().getId(),
                reservation.getUser().getId(),
                TimeUtil.getTimeInStandardFormat(reservation.getReservedAt()),
                TimeUtil.getTimeInStandardFormat(reservation.getExpiresAt()),
                reservation.getStatus(),
                reservation.getQueuePosition()
        );
    }
}
