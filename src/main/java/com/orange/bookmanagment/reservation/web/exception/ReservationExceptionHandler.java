package com.orange.bookmanagment.reservation.web.exception;

import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.exception.BookNotAvailableException;
import com.orange.bookmanagment.reservation.exception.ReservationNotFoundException;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
class ReservationExceptionHandler {

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<HttpResponse> handleReservationNotFoundException(ReservationNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(NOT_FOUND.value())
                        .httpStatus(NOT_FOUND)
                        .message(e.getMessage())
                        .reason("Reservation not found")
                        .build()
        );
    }

    @ExceptionHandler(BookAlreadyReservedException.class)
    public ResponseEntity<HttpResponse> handleBookAlreadyReservedException(BookAlreadyReservedException e) {
        return ResponseEntity.status(BAD_REQUEST).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(BAD_REQUEST.value())
                        .httpStatus(BAD_REQUEST)
                        .message(e.getMessage())
                        .reason("Book is already reserved")
                        .build()
        );
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<HttpResponse> handleBookNotAvailableException(BookNotAvailableException e) {
        return ResponseEntity.status(BAD_REQUEST).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(BAD_REQUEST.value())
                        .httpStatus(BAD_REQUEST)
                        .message(e.getMessage())
                        .reason("Book is not available for reservation")
                        .build()
        );
    }
}
