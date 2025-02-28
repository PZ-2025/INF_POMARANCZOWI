package com.orange.bookmanagment.user.exception;

public class IllegalAccountAccessException extends RuntimeException {
    public IllegalAccountAccessException(String message) {
        super(message);
    }
}
