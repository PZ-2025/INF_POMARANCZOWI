package com.orange.bookmanagment.order.exception;

public class InvalidOrderArgumentException extends RuntimeException {
    public InvalidOrderArgumentException(String message) {
        super(message);
    }
}
