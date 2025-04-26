package com.orange.bookmanagment.loan.service.impl;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.loan.exception.LoanNotFoundException;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.model.enums.LoanStatus;
import com.orange.bookmanagment.loan.repository.LoanRepository;
import com.orange.bookmanagment.loan.service.LoanService;
import com.orange.bookmanagment.reservation.exception.BookNotAvailableException;
import com.orange.bookmanagment.reservation.service.ReservationService;
import com.orange.bookmanagment.shared.events.ReservationCompletedEvent;
import com.orange.bookmanagment.shared.exceptions.BusinessRuleException;
import com.orange.bookmanagment.shared.exceptions.EntityNotFoundException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class LoanServiceImpl implements LoanService {
    private final ApplicationEventPublisher eventPublisher;
    private final LoanRepository loanRepository;
    private final ReservationService reservationService;
    private final BookService bookService;
    private final UserService userService;

    @Override
    @Transactional
    public Loan borrowBook(Long bookId, Long userId, Long librarianId, String notes) {

        if (!bookService.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found with ID: " + bookId);
        }

        // Sprawdzanie czy użytkownik istnieje
        if (!userService.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        // Używamy bookService zamiast repozytorium
        final BookStatus bookStatus = bookService.getBookStatusById(bookId);

        if (bookStatus != BookStatus.AVAILABLE && bookStatus != BookStatus.RESERVED) {
            throw new BusinessRuleException("Book is not available for borrowing");
        }

        // Jeśli książka jest zarezerwowana, sprawdź czy dla tego użytkownika
        if (bookStatus == BookStatus.RESERVED) {
            boolean isReservedForUser = reservationService.isBookReservedForUser(bookId, userId);
            if (!isReservedForUser) {
                throw new BusinessRuleException("Book is reserved for another user");
            }

            // Oznacz rezerwację jako zakończoną
//            reservationService.completeReservation(bookId, userId);
            eventPublisher.publishEvent(new ReservationCompletedEvent(bookId, userId));
        }

        Loan loan = new Loan(bookId, userId, LoanStatus.ACTIVE, librarianId, notes);

        // Aktualizuj status książki przez serwis
//        bookService.updateBookStatus(bookId, BookStatus.BORROWED);
        // Publikuj zdarzenie o zmianie statusu książki zamiast bezpośredniego wywołania
        eventPublisher.publishEvent(new BookStatusChangedEvent(bookId, BookStatus.BORROWED));

        // Publikuj zdarzenie o utworzeniu wypożyczenia. To do notyfikacji
//        eventPublisher.publishEvent(new LoanCreatedEvent(savedLoan.getId(), bookId, userId, librarianId));

        return loanRepository.saveLoan(loan);
    }

    @Override
    @Transactional
    public Loan returnBook(long loanId, Long librarianId) {
        // Używamy userService
        UserType librarianType = userService.getUserTypeById(librarianId);

        if (librarianType != UserType.LIBRARIAN && librarianType != UserType.ADMIN) {
            throw new IllegalArgumentException("Only a librarian can process book returns");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Sprawdź czy książka jest już zwrócona
        if (loan.getStatus() == LoanStatus.RETURNED) {
            return loan;
        }

        // Oznacz wypożyczenie jako zwrócone
        loan.markAsReturned(librarianId);
        loanRepository.saveLoan(loan);

        // Pobierz ID książki
        Long bookId = loan.getBookId();

        // Sprawdź czy są oczekujące rezerwacje przez serwis
        boolean hasActiveReservation = reservationService.processReturnedBook(bookId);

        if (!hasActiveReservation) {
            // Brak rezerwacji, oznacz książkę jako dostępną przez serwis
            bookService.updateBookStatus(bookId, BookStatus.AVAILABLE);
        }

        return loan;
    }

    @Override
    @Transactional
    public Loan extendLoan(long loanId, int days, Long librarianId) {
        // Używamy userService
        UserType librarianType = userService.getUserTypeById(librarianId);

        if (librarianType != UserType.LIBRARIAN && librarianType != UserType.ADMIN) {
            throw new IllegalArgumentException("Only a librarian can extend loans");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Sprawdź czy wypożyczenie jest aktywne
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Cannot extend a loan that is not active or overdue");
        }

        // Przedłuż termin
        loan.extendLoan(days);

        return loanRepository.saveLoan(loan);
    }

    @Override
    @Transactional
    public Loan markBookAsLost(long loanId, String notes, Long librarianId) {
        // Używamy userService
        UserType librarianType = userService.getUserTypeById(librarianId);

        if (librarianType != UserType.LIBRARIAN && librarianType != UserType.ADMIN) {
            throw new IllegalArgumentException("Only a librarian can mark books as lost");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Sprawdź czy to aktywne wypożyczenie
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Only active loans can be marked as lost");
        }

        // Oznacz wypożyczenie jako zgubione
        loan.markAsLost(librarianId, notes);

        // Aktualizuj status książki przez serwis
        Long bookId = loan.getBookId();
        bookService.updateBookStatus(bookId, BookStatus.LOST);

        return loanRepository.saveLoan(loan);
    }

    @Override
    @Transactional
    public List<Loan> getAllActiveLoans() {
        return loanRepository.findByStatusIn(List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }

    @Override
    @Transactional
    public List<Loan> getUserLoans(long userId) {
        return loanRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public List<Loan> getActiveUserLoans(long userId) {
        return loanRepository.findByUserAndStatusIn(userId, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }

    @Override
    public Loan getLoanById(long loanId) throws LoanNotFoundException {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));
    }


//    @Override
//    @Transactional
//    public int updateOverdueLoansStatus() {
//        List<Loan> overdueLoans = loanRepository.findByDueDateBeforeAndStatus(Instant.now(), LoanStatus.ACTIVE);
//
//        for (Loan loan : overdueLoans) {
//            loan.setStatus(LoanStatus.OVERDUE);
//            loan.setUpdatedAt(Instant.now());
//            loanRepository.save(loan);
//        }
//
//        return overdueLoans.size();
//    }

    @Override
    public boolean isBookBorrowedByUser(Long bookId, Long userId) {
        return loanRepository.existsByBookIdAndUserIdAndStatusIn(bookId, userId, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }
}
