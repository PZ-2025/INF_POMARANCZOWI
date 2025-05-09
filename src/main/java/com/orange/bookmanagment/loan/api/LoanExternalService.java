package com.orange.bookmanagment.loan.api;

import com.orange.bookmanagment.loan.api.dto.LoanExternalDto;
import com.orange.bookmanagment.loan.model.Loan;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoanExternalService {
    @Transactional
    List<LoanExternalDto> getAllLoans();

}
