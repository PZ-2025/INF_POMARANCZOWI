package com.orange.bookmanagment.loan.web.model;

import com.orange.bookmanagment.loan.model.enums.LoanStatus;

public record LoanDto(
        long id,
        String bookTitle,
        String userEmail,
        String lendingLibrarianEmail,
        LoanStatus status,
        String notes,
        String borrowedAt,
        String updatedAt,
        String dueDate,
        String returnedAt,
        String returningLibrarianEmail
) {
}
