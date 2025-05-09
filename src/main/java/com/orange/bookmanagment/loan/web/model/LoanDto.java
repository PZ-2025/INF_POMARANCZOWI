package com.orange.bookmanagment.loan.web.model;

import com.orange.bookmanagment.shared.enums.LoanStatus;

public record LoanDto(
        long id,
        long bookId,
        long userId,
        long lendingLibrarianId,
        LoanStatus status,
        String notes,
        String borrowedAt,
        String updatedAt,
        String dueDate,
        String returnedAt,
        long returningLibrarianId
) {
}
