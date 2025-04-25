package com.orange.bookmanagment.reservation.web.controller;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.service.ReservationService;
import com.orange.bookmanagment.reservation.web.mapper.ReservationDtoMapper;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final BookService bookService;
    private final UserService userService;
    private final ReservationDtoMapper reservationDtoMapper;

    //todo: convert to loan

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

        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(HttpResponse.builder()
                            .statusCode(404)
                            .httpStatus(NOT_FOUND)
                            .reason("Book not found")
                            .message("The requested book does not exist.")
                            .build());
        }

        User user = userService.getUserByEmail(authentication.getName());

        Reservation reservation = reservationService.createReservation(book, user);

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
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(HttpResponse.builder()
                            .statusCode(404)
                            .httpStatus(NOT_FOUND)
                            .reason("Reservation not found")
                            .message("The requested reservation does not exist.")
                            .build());
        }

        final User user = userService.getUserByEmail(authentication.getName());
        if (reservation.getUserId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(HttpResponse.builder()
                            .statusCode(403)
                            .httpStatus(FORBIDDEN)
                            .reason("Forbidden")
                            .message("You are not allowed to cancel this reservation.")
                            .build());
        }

        reservationService.cancelReservation(reservation);

        return ResponseEntity.ok(HttpResponse.builder()
                .statusCode(200)
                .httpStatus(OK)
                .reason("Reservation cancelled")
                .message("Reservation cancelled successfully")
                .data(Map.of("reservation", reservationDtoMapper.toDto(reservation)))
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

        final Book book = bookService.getBookById(bookId);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(HttpResponse.builder()
                            .statusCode(404)
                            .httpStatus(NOT_FOUND)
                            .reason("Book not found")
                            .message("The requested book does not exist.")
                            .build());
        }

        final List<Reservation> reservations = reservationService.getBookReservations(book);

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

        final Book book = bookService.getBookById(bookId);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(HttpResponse.builder()
                            .statusCode(404)
                            .httpStatus(NOT_FOUND)
                            .reason("Book not found")
                            .message("The requested book does not exist.")
                            .build());
        }

        final List<Reservation> reservations = reservationService.getActiveBookReservations(book);

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

        final User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(HttpResponse.builder()
                            .statusCode(404)
                            .httpStatus(NOT_FOUND)
                            .reason("User not found")
                            .message("The requested user does not exist.")
                            .build());
        }

        final List<Reservation> reservations = reservationService.getUserReservations(user);

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

        final User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(HttpResponse.builder()
                            .statusCode(404)
                            .httpStatus(NOT_FOUND)
                            .reason("User not found")
                            .message("The requested user does not exist.")
                            .build());
        }

        final List<Reservation> reservations = reservationService.getActiveUserReservations(user);

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

        final User user = userService.getUserByEmail(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(HttpResponse.builder()
                            .statusCode(404)
                            .httpStatus(NOT_FOUND)
                            .reason("User not found")
                            .message("The requested user does not exist.")
                            .build());
        }

        final List<Reservation> reservations = reservationService.getUserReservations(user);

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





}
