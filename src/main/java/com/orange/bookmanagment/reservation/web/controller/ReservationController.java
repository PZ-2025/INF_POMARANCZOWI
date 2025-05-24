package com.orange.bookmanagment.reservation.web.controller;

import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.service.ReservationService;
import com.orange.bookmanagment.reservation.web.mapper.ReservationDtoMapper;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.user.api.UserExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * Kontroler REST obsługujący operacje związane z rezerwacjami książek.
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserExternalService userExternalService;
    private final ReservationDtoMapper reservationDtoMapper;

    /**
     * Tworzy rezerwację dla książki przez zalogowanego użytkownika.
     *
     * @param bookId ID książki do zarezerwowania
     * @param authentication dane użytkownika
     * @return utworzona rezerwacja
     */
    @PostMapping("/book/{bookId}")
    public ResponseEntity<HttpResponse> createReservation(
            @PathVariable("bookId") long bookId,
            Authentication authentication) {

        long userId = userExternalService.getUserIdByEmail(authentication.getName());
        Reservation reservation = reservationService.createReservation(bookId, userId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservation created")
                .message("Reservation created successfully")
                .data(Map.of("reservation", reservationDtoMapper.toDto(reservation)))
                .build());
    }

    /**
     * Anuluje rezerwację.
     *
     * @param reservationId ID rezerwacji do anulowania
     * @param authentication dane użytkownika
     * @return anulowana rezerwacja
     */
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<HttpResponse> cancelReservation(
            @PathVariable("reservationId") long reservationId,
            Authentication authentication) {

        final Reservation reservation = reservationService.getReservationById(reservationId);
        final Reservation cancelReservation = reservationService.cancelReservation(reservation);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservation cancelled")
                .message("Reservation cancelled successfully")
                .data(Map.of("reservation", reservationDtoMapper.toDto(cancelReservation)))
                .build());
    }

    /**
     * Zwraca wszystkie rezerwacje dla danej książki.
     *
     * @param bookId ID książki
     * @return lista rezerwacji
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<HttpResponse> getBookReservations(
            @PathVariable("bookId") long bookId) {

        final List<Reservation> reservations = reservationService.getBookReservations(bookId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservations retrieved")
                .message("Reservations retrieved successfully")
                .data(Map.of("reservations", reservations.stream()
                        .map(reservationDtoMapper::toDto)
                        .toList()))
                .build());
    }

    /**
     * Zwraca aktywne rezerwacje dla danej książki.
     *
     * @param bookId ID książki
     * @return lista aktywnych rezerwacji
     */
    @GetMapping("/book/{bookId}/active")
    public ResponseEntity<HttpResponse> getActiveBookReservations(
            @PathVariable("bookId") long bookId) {

        final List<Reservation> reservations = reservationService.getActiveBookReservations(bookId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Active reservations retrieved")
                .message("Active reservations retrieved successfully")
                .data(Map.of("reservations", reservations.stream()
                        .map(reservationDtoMapper::toDto)
                        .toList()))
                .build());
    }

    /**
     * Zwraca wszystkie rezerwacje danego użytkownika.
     *
     * @param userId ID użytkownika
     * @return lista rezerwacji
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<HttpResponse> getUserReservations(
            @PathVariable("userId") long userId) {

        final List<Reservation> reservations = reservationService.getUserReservations(userId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservations retrieved")
                .message("Reservations retrieved successfully")
                .data(Map.of("reservations", reservations.stream()
                        .map(reservationDtoMapper::toDto)
                        .toList()))
                .build());
    }

    /**
     * Zwraca aktywne rezerwacje użytkownika.
     *
     * @param userId ID użytkownika
     * @return lista aktywnych rezerwacji
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<HttpResponse> getActiveUserReservations(
            @PathVariable("userId") long userId) {

        final List<Reservation> reservations = reservationService.getActiveUserReservations(userId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Active reservations retrieved")
                .message("Active reservations retrieved successfully")
                .data(Map.of("reservations", reservations.stream()
                        .map(reservationDtoMapper::toDto)
                        .toList()))
                .build());
    }

    /**
     * Zwraca rezerwacje zalogowanego użytkownika.
     *
     * @param authentication dane użytkownika
     * @return lista rezerwacji użytkownika
     */
    @GetMapping("/my")
    public ResponseEntity<HttpResponse> getMyReservations(
            Authentication authentication) {

        final long userId = userExternalService.getUserIdByEmail(authentication.getName());
        final List<Reservation> reservations = reservationService.getUserReservations(userId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservations retrieved")
                .message("Reservations retrieved successfully")
                .data(Map.of("reservations", reservations.stream()
                        .map(reservationDtoMapper::toDto)
                        .toList()))
                .build());
    }

    /**
     * Zwraca długość kolejki rezerwacji dla danej książki.
     *
     * @param bookId ID książki
     * @return długość kolejki
     */
    @GetMapping("/queue-length/{bookId}")
    public ResponseEntity<HttpResponse> getQueueLength(
            @PathVariable("bookId") long bookId) {

        int queueLength = reservationService.getQueueLength(bookId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Queue length retrieved")
                .message("Queue length retrieved successfully")
                .data(Map.of("queueLength", queueLength))
                .build());
    }

    /**
     * Wygasza rezerwację.
     *
     * @param reservationId ID rezerwacji
     * @return zaktualizowana rezerwacja
     */
    @PostMapping("/{reservationId}/expire")
    public ResponseEntity<HttpResponse> expireReservation(
            @PathVariable long reservationId) {

        Reservation reservation = reservationService.expireReservation(reservationId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservation expired")
                .message("Reservation marked as expired")
                .data(Map.of("reservation", reservationDtoMapper.toDto(reservation)))
                .build());
    }

    /**
     * Zatwierdza rezerwację jako zrealizowaną.
     *
     * @param reservationId ID rezerwacji
     * @param authentication dane użytkownika
     * @return zaktualizowana rezerwacja
     */
    @PostMapping("/{reservationId}/complete")
    public ResponseEntity<HttpResponse> completeReservation(
            @PathVariable long reservationId,
            Authentication authentication) {

        Reservation reservation = reservationService.completeReservation(reservationId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservation completed")
                .message("Reservation marked as completed")
                .data(Map.of("reservation", reservationDtoMapper.toDto(reservation)))
                .build());
    }

    /**
     * Przedłuża ważność rezerwacji.
     *
     * @param reservationId ID rezerwacji
     * @return zaktualizowana rezerwacja
     */
    @PostMapping("/{reservationId}/extend")
    public ResponseEntity<HttpResponse> extendReservation(
            @PathVariable long reservationId) {

        Reservation reservation = reservationService.extendReservation(reservationId);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservation extended")
                .message("Reservation expiration extended")
                .data(Map.of("reservation", reservationDtoMapper.toDto(reservation)))
                .build());
    }


}
