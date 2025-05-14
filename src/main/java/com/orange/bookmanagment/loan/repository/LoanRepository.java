package com.orange.bookmanagment.loan.repository;

import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.shared.enums.LoanStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium pośredniczące w dostępie do wypożyczeń.
 * Używa {@link LoanJpaRepository} do wykonywania operacji na bazie danych.
 */
@Repository
@AllArgsConstructor
public class LoanRepository {

    private final LoanJpaRepository loanJpaRepository;

    /**
     * Sprawdza, czy istnieje wypożyczenie dla danego użytkownika i książki w określonych statusach.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @param statuses lista statusów wypożyczenia
     * @return true, jeśli istnieje takie wypożyczenie; false w przeciwnym razie
     */
    public boolean existsByBookIdAndUserIdAndStatusIn(long bookId, long userId, List<LoanStatus> statuses) {
        return loanJpaRepository.existsByBookIdAndUserIdAndStatusIn(bookId, userId, statuses);
    }

    /**
     * Zapisuje wypożyczenie do bazy danych.
     *
     * @param loan encja wypożyczenia
     * @return zapisana encja
     */
    public Loan saveLoan(Loan loan) {
        return loanJpaRepository.save(loan);
    }

    /**
     * Wyszukuje wypożyczenie po ID.
     *
     * @param id identyfikator wypożyczenia
     * @return opcjonalna encja wypożyczenia
     */
    public Optional<Loan> findById(long id) {
        return loanJpaRepository.findById(id);
    }

    /**
     * Zwraca wszystkie wypożyczenia o określonych statusach.
     *
     * @param statuses lista statusów
     * @return lista wypożyczeń
     */
    public List<Loan> findByStatusIn(List<LoanStatus> statuses) {
        return loanJpaRepository.findByStatusIn(statuses);
    }

    /**
     * Zwraca wszystkie wypożyczenia danego użytkownika.
     *
     * @param userId identyfikator użytkownika
     * @return lista wypożyczeń użytkownika
     */
    public List<Loan> findByUserId(long userId) {
        return loanJpaRepository.findByUserId(userId);
    }

    /**
     * Zwraca wszystkie wypożyczenia użytkownika z określonymi statusami.
     *
     * @param userId identyfikator użytkownika
     * @param statuses lista statusów
     * @return lista wypożyczeń użytkownika z podanymi statusami
     */
    public List<Loan> findByUserAndStatusIn(long userId, List<LoanStatus> statuses) {
        return loanJpaRepository.findByUserIdAndStatusIn(userId, statuses);
    }

    /**
     * Zwraca wszystkie wypożyczenia w systemie.
     *
     * @return lista wszystkich wypożyczeń
     */
    public List<Loan> findAll() {
        return loanJpaRepository.findAll();
    }

    /**
     * Sprawdza, czy istnieje co najmniej jedno aktywne wypożyczenie danej książki o jednym z podanych statusów.
     *
     * @param bookId ID książki
     * @param statuses lista statusów wypożyczenia do sprawdzenia
     * @return true, jeśli istnieje takie wypożyczenie; false w przeciwnym razie
     */
    public boolean existsByBookIdAndStatusIn(long bookId, List<LoanStatus> statuses) {
        return loanJpaRepository.existsByBookIdAndStatusIn(bookId, statuses);
    }
}
