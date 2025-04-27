package com.orange.bookmanagment.loan.service.impl;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.book.api.BookInternalDto;
import com.orange.bookmanagment.loan.api.LoanExternalService;
import com.orange.bookmanagment.loan.exception.BookNotAvailableException;
import com.orange.bookmanagment.reservation.api.ReservationExternalService;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.model.enums.LoanStatus;
import com.orange.bookmanagment.loan.repository.LoanRepository;
import com.orange.bookmanagment.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class LoanServiceImpl implements LoanService, LoanExternalService {
    private final LoanRepository loanRepository;
    private final BookExternalService bookExternalService;
    private final ReservationExternalService reservationExternalService;
//    private final UserService userService;

    @Override
    @Transactional
    public Loan borrowBook(Long bookId, Long userId, Long librarianId, String notes) {
        BookInternalDto book = bookExternalService.getBookForExternal(bookId);

        if (book.status() == BookStatus.RESERVED) {
            boolean isReservedForUser = reservationExternalService.isBookReservedForUser(bookId, userId);
            if (!isReservedForUser) {
                throw new BookNotAvailableException("Book is reserved for another user");
            }
            reservationExternalService.completeReservation(bookId, userId);
        }

        Loan loan = new Loan(bookId, userId, LoanStatus.ACTIVE, librarianId, notes);
        bookExternalService.updateBookStatus(bookId, BookStatus.BORROWED);

        return loanRepository.saveLoan(loan);
    }

//    @Override
//    @Transactional
//    public Loan returnBook(long loanId, Long librarianId) {
//        // Używamy userService
//        UserType librarianType = userService.getUserTypeById(librarianId);
//
//        if (librarianType != UserType.LIBRARIAN && librarianType != UserType.ADMIN) {
//            throw new IllegalArgumentException("Only a librarian can process book returns");
//        }
//
//        Loan loan = loanRepository.findById(loanId)
//                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));
//
//        // Sprawdź czy książka jest już zwrócona
//        if (loan.getStatus() == LoanStatus.RETURNED) {
//            return loan;
//        }
//
//        // Oznacz wypożyczenie jako zwrócone
//        loan.markAsReturned(librarianId);
//        loanRepository.saveLoan(loan);
//
//        // Pobierz ID książki
//        Long bookId = loan.getBookId();
//
//        // Sprawdź czy są oczekujące rezerwacje przez serwis
//        boolean hasActiveReservation = reservationService.processReturnedBook(bookId);
//
//        if (!hasActiveReservation) {
//            // Brak rezerwacji, oznacz książkę jako dostępną przez serwis
//           // bookService.updateBookStatus(bookId, BookStatus.AVAILABLE);
//            eventPublisher.publishEvent(new LoanBookEvent(bookId, LoanBookEvent.BookStatus.AVAILABLE));
//        }
//
//        return loan;
//    }
//
//    @Override
//    @Transactional
//    public Loan extendLoan(long loanId, int days, Long librarianId) {
//        // Używamy userService
//        Loan loan = loanRepository.findById(loanId)
//                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));
//
//        // Sprawdź czy wypożyczenie jest aktywne
//        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
//            throw new IllegalStateException("Cannot extend a loan that is not active or overdue");
//        }
//
//        // Przedłuż termin
//        loan.extendLoan(days);
//
//        return loanRepository.saveLoan(loan);
//    }
//
//    @Override
//    @Transactional
//    public Loan markBookAsLost(long loanId, String notes, Long librarianId) {
//        // Używamy userService
//        UserType librarianType = userService.getUserTypeById(librarianId);
//
//        if (librarianType != UserType.LIBRARIAN && librarianType != UserType.ADMIN) {
//            throw new IllegalArgumentException("Only a librarian can mark books as lost");
//        }
//
//        Loan loan = loanRepository.findById(loanId)
//                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));
//
//        // Sprawdź czy to aktywne wypożyczenie
//        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
//            throw new IllegalStateException("Only active loans can be marked as lost");
//        }
//
//        // Oznacz wypożyczenie jako zgubione
//        loan.markAsLost(librarianId, notes);
//
//        // Aktualizuj status książki przez serwis
//        Long bookId = loan.getBookId();
//        //bookService.updateBookStatus(bookId, BookStatus.LOST);
//        eventPublisher.publishEvent(new LoanBookEvent(bookId, LoanBookEvent.BookStatus.LOST));
//
//        return loanRepository.saveLoan(loan);
//    }
//
//    @Override
//    @Transactional
//    public List<Loan> getAllActiveLoans() {
//        return loanRepository.findByStatusIn(List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
//    }
//
//    @Override
//    @Transactional
//    public List<Loan> getUserLoans(long userId) {
//        return loanRepository.findByUserId(userId);
//    }
//
//    @Override
//    @Transactional
//    public List<Loan> getActiveUserLoans(long userId) {
//        return loanRepository.findByUserAndStatusIn(userId, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
//    }
//
//    @Override
//    public Loan getLoanById(long loanId) throws LoanNotFoundException {
//        return loanRepository.findById(loanId)
//                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));
//    }
//
//
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
//
    @Override
    public boolean isBookBorrowedByUser(Long bookId, Long userId) {
        return loanRepository.existsByBookIdAndUserIdAndStatusIn(bookId, userId, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }
}
