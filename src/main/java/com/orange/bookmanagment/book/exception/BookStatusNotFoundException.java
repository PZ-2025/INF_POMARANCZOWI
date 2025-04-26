package com.orange.bookmanagment.book.exception;

public class BookStatusNotFoundException extends RuntimeException {
    public BookStatusNotFoundException(String message) {
        super(message);
    }
}
