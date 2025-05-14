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

/**
 * Obsługuje wyjątki związane z rezerwacjami i zwraca odpowiednie odpowiedzi HTTP z komunikatem o błędzie.
 */
@ControllerAdvice
class ReservationExceptionHandler {

    /**
     * Obsługuje wyjątek rezerwacji, która nie została znaleziona.
     *
     * @param e wyjątek ReservationNotFoundException
     * @return odpowiedź HTTP 404 z komunikatem o błędzie
     */
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

    /**
     * Obsługuje wyjątek, gdy książka jest już zarezerwowana przez danego użytkownika.
     *
     * @param e wyjątek BookAlreadyReservedException
     * @return odpowiedź HTTP 400 z komunikatem o błędzie
     */
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

    /**
     * Obsługuje wyjątek, gdy książka nie jest dostępna do rezerwacji.
     *
     * @param e wyjątek BookNotAvailableException
     * @return odpowiedź HTTP 400 z komunikatem o błędzie
     */
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
