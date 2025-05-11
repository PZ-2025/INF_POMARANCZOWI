package com.orange.bookmanagment.loan.web.mapper;

import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.web.model.LoanDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {
    public LoanDto toDto(Loan loan) {
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
                loan.getExtendedCount()
        );
    }
}
