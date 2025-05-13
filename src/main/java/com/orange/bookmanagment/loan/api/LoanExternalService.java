package com.orange.bookmanagment.loan.api;

import com.orange.bookmanagment.loan.api.dto.LoanExternalDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interfejs serwisu zewnętrznego do obsługi wypożyczeń książek.
 * Umożliwia pobieranie listy wypożyczeń do użytku zewnętrznego.
 */
public interface LoanExternalService {

    /**
     * Zwraca listę wszystkich wypożyczeń w systemie.
     *
     * @return lista wypożyczeń w formacie DTO
     */
    @Transactional
    List<LoanExternalDto> getAllLoans();
}
