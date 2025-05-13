package com.orange.bookmanagment.loan.api.dto;

import com.orange.bookmanagment.shared.enums.LoanStatus;

import java.time.Instant;

public record LoanExternalDto(
        Long id,
        Long bookId,
        Long userId,
        LoanStatus status,
        String notes,
        Long lendingLibrarianId,
        Instant borrowedAt,
        Long returningLibrarianId,
        Instant returnedAt,
        Instant dueDate,
        Instant updatedAt
) { }
