package com.orange.bookmanagment.loan.service.mapper;

import com.orange.bookmanagment.loan.api.dto.LoanExternalDto;
import com.orange.bookmanagment.loan.model.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanInternalMapper {
    public LoanExternalDto toDto(Loan loan) {
        if (loan == null) {
            return null;
        }

        return new LoanExternalDto(
                loan.getId(),
                loan.getBookId(),
                loan.getUserId(),
                loan.getStatus(),
                loan.getNotes(),
                loan.getLendingLibrarianId(),
                loan.getBorrowedAt(),
                loan.getReturningLibrarianId(),
                loan.getReturnedAt(),
                loan.getDueDate(),
                loan.getUpdatedAt()
        );
    }
}
