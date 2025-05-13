package com.orange.bookmanagment.loan.web.requests;

public record CreateLoanRequest(
        Long bookId,
        Long userId,
        String notes
) { }
