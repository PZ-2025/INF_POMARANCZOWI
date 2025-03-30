package com.orange.bookmanagment.reservation.exception;

public class BookAlreadyReservedException extends RuntimeException {
    public BookAlreadyReservedException(String message) {
        super(message);
    }
}
