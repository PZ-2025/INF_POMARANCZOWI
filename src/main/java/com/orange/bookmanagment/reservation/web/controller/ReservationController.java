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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/reservations")
 @RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final BookService bookService;
    private final UserService userService;
    private final ReservationDtoMapper reservationDtoMapper;


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
                .data(Map.of("reservation", reservationDtoMapper.toDto(reservation)))//todo: add reservation mapper
                .build());
    }

}
