package com.orange.bookmanagment.reservation.service.mapper;

import com.orange.bookmanagment.reservation.api.dto.ReservationInternalDto;
import com.orange.bookmanagment.reservation.model.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationInternalMapper {

    public ReservationInternalDto toDto(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        return new ReservationInternalDto(
                reservation.getId(),
                reservation.getBookId(),
                reservation.getUserId(),
                reservation.getStatus(),
                reservation.getQueuePosition(),
                reservation.getReservedAt(),
                reservation.getExpiresAt(),
                reservation.getUpdatedAt()
        );
    }

    public List<ReservationInternalDto> toDtoList(List<Reservation> reservations) {
        if (reservations == null) {
            return List.of();
        }

        return reservations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}