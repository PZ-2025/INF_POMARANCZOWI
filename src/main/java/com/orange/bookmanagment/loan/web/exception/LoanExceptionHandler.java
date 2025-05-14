package com.orange.bookmanagment.loan.web.exception;

import com.orange.bookmanagment.loan.exception.BookNotAvailableException;
import com.orange.bookmanagment.loan.exception.LoanNotFoundException;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Globalna obsługa wyjątków związanych z wypożyczeniami.
 */
@ControllerAdvice
class LoanExceptionHandler {

    /**
     * Obsługuje wyjątek, gdy wypożyczenie nie zostało znalezione.
     *
     * @param e wyjątek
     * @return odpowiedź HTTP 404
     */
    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<HttpResponse> handleLoanNotFoundException(LoanNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(NOT_FOUND.value())
                        .httpStatus(NOT_FOUND)
                        .message(e.getMessage())
                        .reason("Loan not found")
                        .build()
        );
    }

    /**
     * Obsługuje wyjątek, gdy książka nie jest dostępna do wypożyczenia.
     *
     * @param e wyjątek
     * @return odpowiedź HTTP 400
     */
    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<HttpResponse> handleBookNotAvailableException(BookNotAvailableException e) {
        return ResponseEntity.status(BAD_REQUEST).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(BAD_REQUEST.value())
                        .httpStatus(BAD_REQUEST)
                        .message(e.getMessage())
                        .reason("Book is not available for loan")
                        .build()
        );
    }
}
