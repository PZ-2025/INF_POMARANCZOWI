package com.orange.bookmanagment.reservation.service.mapper;

import com.orange.bookmanagment.reservation.api.dto.ReservationExternalDto;
import com.orange.bookmanagment.reservation.model.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper odpowiedzialny za konwersję encji {@link Reservation}
 * na obiekty DTO wykorzystywane w komunikacji między modułami.
 */
@Component
public class ReservationInternalMapper {

    /**
     * Konwertuje pojedynczą rezerwację do DTO na potrzeby warstwy zewnętrznej.
     *
     * @param reservation encja rezerwacji
     * @return DTO reprezentujący rezerwację
     */
    public ReservationExternalDto toDto(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        return new ReservationExternalDto(
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

    /**
     * Konwertuje listę rezerwacji na listę DTO.
     *
     * @param reservations lista encji rezerwacji
     * @return lista DTO rezerwacji
     */
    public List<ReservationExternalDto> toDtoList(List<Reservation> reservations) {
        if (reservations == null) {
            return List.of();
        }

        return reservations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}