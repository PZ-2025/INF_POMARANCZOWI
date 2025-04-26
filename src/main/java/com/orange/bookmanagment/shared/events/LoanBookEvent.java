package com.orange.bookmanagment.shared.events;

public record LoanBookEvent(
        long bookId,
        BookStatus bookStatus
) {
    public enum BookStatus{
        AVAILABLE, BORROWED, RESERVED, LOST
    }
}

