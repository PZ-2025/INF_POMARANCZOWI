package com.orange.bookmanagment.loan.service;

import com.orange.bookmanagment.loan.exception.LoanNotFoundException;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.exception.BookNotAvailableException;

import java.util.List;

/**
 * Interfejs serwisu do zarządzania wypożyczeniami książek.
 */
public interface LoanService {

    /**
     * Wypożycza książkę (obsługiwane przez bibliotekarza).
     *
     * @param bookId      ID książki
     * @param userId      ID użytkownika
     * @param librarianId ID bibliotekarza
     * @param notes       notatki do wypożyczenia
     * @return utworzone wypożyczenie
     * @throws BookNotAvailableException gdy książka nie jest dostępna
     * @throws IllegalArgumentException gdy bibliotekarz jest nieprawidłowy
     */
    Loan borrowBook(Long bookId, Long userId, Long librarianId, String notes) throws BookNotAvailableException, IllegalArgumentException;

    /**
     * Zwraca książkę (obsługiwane przez bibliotekarza).
     *
     * @param loanId      ID wypożyczenia
     * @param librarianId ID bibliotekarza
     * @return zaktualizowane wypożyczenie
     * @throws LoanNotFoundException gdy wypożyczenie nie istnieje
     * @throws IllegalArgumentException gdy bibliotekarz jest nieprawidłowy
     */
    Loan returnBook(long loanId, Long librarianId) throws LoanNotFoundException, IllegalArgumentException;

    /**
     * Przedłuża wypożyczenie (obsługiwane przez bibliotekarza).
     *
     * @param loanId      ID wypożyczenia
     * @param librarianId ID bibliotekarza
     * @return zaktualizowane wypożyczenie
     * @throws LoanNotFoundException gdy wypożyczenie nie istnieje
     * @throws IllegalArgumentException gdy bibliotekarz jest nieprawidłowy
     */
    Loan extendLoan(long loanId, long librarianId) throws LoanNotFoundException, IllegalArgumentException;

    /**
     * Zgłasza książkę jako zagubioną (obsługiwane przez bibliotekarza).
     *
     * @param loanId      ID wypożyczenia
     * @param notes       notatki dot. zagubienia
     * @param librarianId ID bibliotekarza
     * @return zaktualizowane wypożyczenie
     * @throws LoanNotFoundException gdy wypożyczenie nie istnieje
     * @throws IllegalArgumentException gdy bibliotekarz jest nieprawidłowy
     */
    Loan markBookAsLost(long loanId, String notes, Long librarianId) throws LoanNotFoundException, IllegalArgumentException;

    /**
     * Zwraca wszystkie aktywne wypożyczenia.
     *
     * @return lista aktywnych wypożyczeń
     */
    List<Loan> getAllActiveLoans();

    /**
     * Zwraca wszystkie wypożyczenia użytkownika.
     *
     * @param userId ID użytkownika
     * @return lista wypożyczeń
     */
    List<Loan> getUserLoans(long userId);

    /**
     * Zwraca tylko aktywne wypożyczenia użytkownika.
     *
     * @param userId ID użytkownika
     * @return lista aktywnych wypożyczeń
     */
    List<Loan> getActiveUserLoans(long userId);

    /**
     * Zwraca wypożyczenie po ID.
     *
     * @param loanId ID wypożyczenia
     * @return wypożyczenie
     * @throws LoanNotFoundException jeśli nie znaleziono wypożyczenia
     */
    Loan getLoanById(long loanId) throws LoanNotFoundException;
}
