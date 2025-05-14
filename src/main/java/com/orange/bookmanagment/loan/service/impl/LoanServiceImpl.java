package com.orange.bookmanagment.loan.service.impl;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.loan.api.LoanExternalService;
import com.orange.bookmanagment.loan.api.dto.LoanExternalDto;
import com.orange.bookmanagment.loan.exception.BookNotAvailableException;
import com.orange.bookmanagment.loan.exception.LoanNotFoundException;
import com.orange.bookmanagment.loan.service.mapper.LoanInternalMapper;
import com.orange.bookmanagment.reservation.api.ReservationExternalService;
import com.orange.bookmanagment.reservation.api.dto.ReservationExternalDto;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.shared.enums.LoanStatus;
import com.orange.bookmanagment.loan.repository.LoanRepository;
import com.orange.bookmanagment.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementacja interfejsu {@link LoanService} oraz {@link LoanExternalService}.
 * <p>
 * Odpowiada za logikę wypożyczeń, w tym: tworzenie, zwroty, przedłużenia i obsługę rezerwacji.
 */
@Service
@RequiredArgsConstructor
class LoanServiceImpl implements LoanService, LoanExternalService {

    private final LoanRepository loanRepository;
    private final BookExternalService bookExternalService;
    private final ReservationExternalService reservationExternalService;
    private final LoanInternalMapper loanInternalMapper;

    /**
     * Wypożycza książkę użytkownikowi.
     * Jeśli książka jest zarezerwowana – sprawdza zgodność z użytkownikiem i kończy rezerwację.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @param librarianId ID bibliotekarza
     * @param notes notatki do wypożyczenia
     * @return utworzone wypożyczenie
     */
    @Override
    @Transactional
    public Loan borrowBook(Long bookId, Long userId, Long librarianId, String notes) {
        BookExternalDto book = bookExternalService.getBookForExternal(bookId);

        if (book.status() == BookStatus.RESERVED) {
            boolean isReservedForUser = reservationExternalService.isBookReservedForUser(bookId, userId);
            if (!isReservedForUser) {
                throw new BookNotAvailableException("Book is reserved for another user");
            }
            reservationExternalService.completeReservation(bookId, userId);
        }

        if (book.status() == BookStatus.BORROWED) {
            throw new BookNotAvailableException("Book is already borrowed");
        }

        Loan loan = new Loan(bookId, userId, LoanStatus.ACTIVE, librarianId, notes);
        bookExternalService.updateBookStatus(bookId, BookStatus.BORROWED);

        return loanRepository.saveLoan(loan);
    }

    /**
     * Zwraca książkę i obsługuje zmianę statusu oraz ewentualne przypisanie kolejnej rezerwacji.
     *
     * @param loanId ID wypożyczenia
     * @param librarianId ID bibliotekarza
     * @return zaktualizowane wypożyczenie
     */
    @Override
    @Transactional
    public Loan returnBook(long loanId, Long librarianId) {
        final Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            return loan;
        }

        loan.markAsReturned(librarianId);
        loanRepository.saveLoan(loan);

        final Long bookId = loan.getBookId();

        // Pobranie wszystkich oczekujących rezerwacji
        List<ReservationExternalDto> reservations = reservationExternalService.getPendingReservations(bookId);

        if (reservations.isEmpty()) {
            boolean hasOtherActiveLoans = loanRepository.existsByBookIdAndStatusIn(
                    bookId, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE)
            );

            if (hasOtherActiveLoans) {
                bookExternalService.updateBookStatus(bookId, BookStatus.BORROWED);
            } else {
                bookExternalService.updateBookStatus(bookId, BookStatus.AVAILABLE);
            }

            return loan;
        }

        if (reservations.size() == 1) {
            // Tylko jedna rezerwacja – nowe wypożyczenie
            var r = reservations.get(0);
            reservationExternalService.markAsReady(r.id());
            bookExternalService.updateBookStatus(bookId, BookStatus.BORROWED);

            // Tworzenie nowego wypożyczenia
            Loan newLoan = new Loan(bookId, r.userId(), LoanStatus.ACTIVE, librarianId, "Loan from reservation");
            return loanRepository.saveLoan(newLoan);
        }

        // Więcej niż jedna rezerwacja
        var first = reservations.get(0);
        reservationExternalService.markAsReady(first.id());
        bookExternalService.updateBookStatus(bookId, BookStatus.RESERVED);
        for (int i = 1; i < reservations.size(); i++) {
            reservationExternalService.decrementQueuePosition(reservations.get(i).id());
        }

        return loan;
    }

    /**
     * Przedłuża wypożyczenie o 30 dni, jeśli książka nie została zarezerwowana przez innego użytkownika.
     *
     * @param loanId ID wypożyczenia
     * @param librarianId ID bibliotekarza zatwierdzającego przedłużenie
     * @return zaktualizowane wypożyczenie
     */
    @Override
    @Transactional
    public Loan extendLoan(long loanId, long librarianId) {
        // Używamy userService
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        if (reservationExternalService.isReservedByAnotherUser(loan.getBookId(), loan.getUserId())) {
            throw new IllegalStateException("Cannot extend, the book is reserved by another user.");
        }

        // Sprawdź, czy wypożyczenie jest aktywne
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Cannot extend a loan that is not active or overdue");
        }

        // Przedłuż termin
        loan.extendLoan();

        return loanRepository.saveLoan(loan);
    }

    /**
     * Oznacza wypożyczenie jako zgubione i aktualizuje status książki na LOST.
     *
     * @param loanId ID wypożyczenia
     * @param notes notatki dotyczące zgubienia
     * @param librarianId ID bibliotekarza zgłaszającego
     * @return zaktualizowane wypożyczenie
     */
    @Override
    @Transactional
    public Loan markBookAsLost(long loanId, String notes, Long librarianId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Sprawdź, czy to aktywne wypożyczenie
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Only active loans can be marked as lost");
        }

        // Oznaczenie wypożyczenia jako zgubione
        loan.markAsLost(librarianId, notes);
        long bookId = loan.getBookId();

        // Anulowanie wszystkich aktywnych rezerwacji (PENDING i READY)
        List<ReservationExternalDto> reservations = reservationExternalService.getActiveBookReservationsForMark(bookId);
        for (ReservationExternalDto reservation : reservations) {
            reservationExternalService.cancelReservationForMark(reservation.id());
        }

        // Aktualizacja statusu książki
        bookExternalService.updateBookStatus(bookId, BookStatus.LOST);

        return loanRepository.saveLoan(loan);
    }

    /**
     * Zwraca wszystkie aktywne wypożyczenia (ACTIVE i OVERDUE).
     *
     * @return lista wypożyczeń
     */
    @Override
    @Transactional
    public List<Loan> getAllActiveLoans() {
        return loanRepository.findByStatusIn(List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }

    /**
     * Zwraca wszystkie wypożyczenia danego użytkownika.
     *
     * @param userId ID użytkownika
     * @return lista wypożyczeń
     */
    @Override
    @Transactional
    public List<Loan> getUserLoans(long userId) {
        return loanRepository.findByUserId(userId);
    }

    /**
     * Zwraca aktywne wypożyczenia użytkownika (ACTIVE lub OVERDUE).
     *
     * @param userId ID użytkownika
     * @return lista wypożyczeń
     */
    @Override
    @Transactional
    public List<Loan> getActiveUserLoans(long userId) {
        return loanRepository.findByUserAndStatusIn(userId, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }

    /**
     * Zwraca wypożyczenie na podstawie ID.
     *
     * @param loanId ID wypożyczenia
     * @return wypożyczenie
     */
    @Override
    public Loan getLoanById(long loanId) throws LoanNotFoundException {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));
    }

//    todo: po jakimś czasie sprawdzać przedawnienia
////    @Override
////    @Transactional
////    public int updateOverdueLoansStatus() {
////        List<Loan> overdueLoans = loanRepository.findByDueDateBeforeAndStatus(Instant.now(), LoanStatus.ACTIVE);
////
////        for (Loan loan : overdueLoans) {
////            loan.setStatus(LoanStatus.OVERDUE);
////            loan.setUpdatedAt(Instant.now());
////            loanRepository.save(loan);
////        }
////
////        return overdueLoans.size();
////    }

    /**
     * Zwraca wszystkie wypożyczenia w systemie w formacie DTO.
     *
     * @return lista wypożyczeń DTO
     */
    @Override
    @Transactional
    public List<LoanExternalDto> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanInternalMapper::toDto)
                .toList();
    }
}
