package com.orange.bookmanagment.order.web.exception;

import com.orange.bookmanagment.order.exception.InvalidOrderArgumentException;
import com.orange.bookmanagment.order.exception.OrderAlreadyExistException;
import com.orange.bookmanagment.order.exception.OrderNotFoundException;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
class OrderExceptionHandler {

    @ExceptionHandler(InvalidOrderArgumentException.class)
    public ResponseEntity<HttpResponse> handleInvalidOrderArgumentException(InvalidOrderArgumentException e) {
        return ResponseEntity.status(BAD_REQUEST).body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .reason("Invalid order argument!")
                        .message(e.getMessage())
                        .httpStatus(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<HttpResponse> handleOrderNotFoundException(OrderNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(HttpResponse.builder()
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .reason("Order has not been found!")
                .message(e.getMessage())
                .httpStatus(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build());
    }

    @ExceptionHandler(OrderAlreadyExistException.class)
    public ResponseEntity<HttpResponse> handleOrderAlreadyExistException(OrderAlreadyExistException e) {
        return ResponseEntity.status(BAD_REQUEST).body(HttpResponse.builder()
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .reason("Order already exist!")
                .message(e.getMessage())
                .httpStatus(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build());
    }
}
