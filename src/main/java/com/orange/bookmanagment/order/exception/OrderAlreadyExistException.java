package com.orange.bookmanagment.order.exception;

public class OrderAlreadyExistException extends RuntimeException {
    public OrderAlreadyExistException(String message) {
        super(message);
    }
}
