package com.orange.bookmanagment.loan.service.mapper;

import com.orange.bookmanagment.loan.api.dto.LoanExternalDto;
import com.orange.bookmanagment.loan.model.Loan;
import org.springframework.stereotype.Component;

/**
 * Mapper konwertujący encję {@link Loan} na DTO {@link LoanExternalDto}
 * wykorzystywane do komunikacji z systemami zewnętrznymi.
 */
@Component
public class LoanInternalMapper {

    /**
     * Mapuje encję wypożyczenia na obiekt DTO.
     *
     * @param loan encja wypożyczenia
     * @return DTO wypożyczenia lub null, jeśli wejście było null
     */
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
