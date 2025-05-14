package com.orange.bookmanagment.loan.web.mapper;

import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.web.model.LoanDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.reservation.api.ReservationExternalService;
import org.springframework.stereotype.Component;

/**
 * Mapper odpowiedzialny za konwersję encji {@link Loan} na obiekt {@link LoanDto}
 * używany w odpowiedziach API.
 */
@Component
public class LoanMapper {

    private final ReservationExternalService reservationExternalService;

    public LoanMapper(ReservationExternalService reservationExternalService) {
        this.reservationExternalService = reservationExternalService;
    }

    /**
     * Mapuje encję wypożyczenia na DTO, uwzględniając informację,
     * czy książka jest zarezerwowana przez innego użytkownika.
     *
     * @param loan encja wypożyczenia
     * @return DTO wypożyczenia
     */
    public LoanDto toDto(Loan loan) {
        boolean isReserved = reservationExternalService.isBookReservedForAnotherUser(
                loan.getBookId(),
                loan.getUserId()
        );

        return new LoanDto(
                loan.getId(),
                loan.getBookId(),
                loan.getUserId(),
                loan.getLendingLibrarianId(),
                loan.getStatus(),
                loan.getNotes(),
                TimeUtil.getTimeInStandardFormat(loan.getBorrowedAt()),
                TimeUtil.getTimeInStandardFormat(loan.getUpdatedAt()),
                TimeUtil.getTimeInStandardFormat(loan.getDueDate()),
                TimeUtil.getTimeInStandardFormat(loan.getReturnedAt()),
                loan.getReturningLibrarianId(),
                loan.getExtendedCount(),
                isReserved
        );
    }
}
