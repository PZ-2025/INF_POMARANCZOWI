package com.orange.bookmanagment.book.web.exception;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.shared.model.HttpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

/**
 * Globalny handler wyjątków związanych z książkami.
 * Przechwytuje wyjątki i zwraca ustandaryzowaną odpowiedź HTTP.
 */
@ControllerAdvice
class BookExceptionHandler {

    /**
     * Obsługuje wyjątek {@link BookNotFoundException}.
     *
     * @param e wyjątek informujący o braku książki
     * @return odpowiedź HTTP 404 z informacją o błędzie
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<HttpResponse> handleBookNotFoundException(BookNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(HttpResponse
                .builder()
                        .message(e.getMessage())
                        .reason("Book has not been found")
                        .statusCode(NOT_FOUND.value())
                        .httpStatus(NOT_FOUND)
                .build());
    }
}
