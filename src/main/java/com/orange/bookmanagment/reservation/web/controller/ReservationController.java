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
 * ReservationController handles the reservation-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/reservations")
 @RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserExternalService userExternalService;
    private final ReservationDtoMapper reservationDtoMapper;

    /**
     * Creates a reservation for a book.
     *
     * @param bookId        the ID of the book to reserve
     * @param authentication the authentication object containing user details
     * @return a ResponseEntity containing the reservation details or an error message
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
     * Cancels a reservation.
     *
     * @param reservationId  the ID of the reservation to cancel
     * @param authentication the authentication object containing user details
     * @return a ResponseEntity indicating the result of the cancellation
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
     * <p>Get all reservations for a book (librarian only)</p>
     *
     * @param bookId ID of the book
     * @return list of reservations for the book
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
     * <p>Get active reservations for a book (librarian only)</p>
     *
     * @param bookId ID of the book
     * @return list of active reservations for the book
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
     * <p>Get all reservations for a user (librarian or self)</p>
     *
     * @param userId ID of the user
     * @return list of reservations for the user
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

    @PostMapping("/{reservationId}/expire")
    public ResponseEntity<HttpResponse> expireReservation(@PathVariable long reservationId) {
        Reservation reservation = reservationService.expireReservation(reservationId);
        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservation expired")
                .message("Reservation marked as expired")
                .data(Map.of("reservation", reservationDtoMapper.toDto(reservation)))
                .build());
    }

    @PostMapping("/{reservationId}/complete")
    public ResponseEntity<HttpResponse> completeReservation(@PathVariable long reservationId, Authentication authentication) {
        Reservation reservation = reservationService.completeReservation(reservationId);
        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservation completed")
                .message("Reservation marked as completed")
                .data(Map.of("reservation", reservationDtoMapper.toDto(reservation)))
                .build());
    }

    @PostMapping("/{reservationId}/extend")
    public ResponseEntity<HttpResponse> extendReservation(@PathVariable long reservationId) {
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
