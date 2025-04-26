package com.orange.bookmanagment.book.web.exception;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.shared.model.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
class BookExceptionHandler {

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
