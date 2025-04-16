package com.orange.bookmanagment.loan.service.impl;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.loan.exception.LoanNotFoundException;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.model.enums.LoanStatus;
import com.orange.bookmanagment.loan.repository.LoanRepository;
import com.orange.bookmanagment.loan.service.LoanService;
import com.orange.bookmanagment.reservation.exception.BookNotAvailableException;
import com.orange.bookmanagment.reservation.service.ReservationService;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
//    private final BookRepository bookRepository;
    private final ReservationService reservationService;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Loan borrowBook(Book book, User user, User librarian, String notes) {

        if (librarian.getUserType() != UserType.LIBRARIAN && librarian.getUserType() != UserType.ADMIN) {
            throw new IllegalArgumentException("Only a librarian can process book loans");
        }
        if (book.getStatus() != BookStatus.AVAILABLE && book.getStatus() != BookStatus.RESERVED) {
            throw new BookNotAvailableException("Book is not available for borrowing");
        }

        // If book is reserved, check if it's reserved for this user
        if (book.getStatus() == BookStatus.RESERVED) {
            boolean isReservedForUser = reservationService.isBookReservedForUser(book, user);
            if (!isReservedForUser) {
                throw new BookNotAvailableException("Book is reserved for another user");
            }

            // Mark the reservation as completed
            reservationService.completeReservation(book, user);
        }

        Loan loan = new Loan(book, user, LoanStatus.ACTIVE, librarian, notes);

        book.setStatus(BookStatus.BORROWED);
        bookRepository.saveBook(book);

        return loanRepository.saveLoan(loan);
    }

    @Override
    @Transactional
    public Loan returnBook(long loanId, User librarian) {
        // Verify librarian has proper role
        if (librarian.getUserType() != UserType.LIBRARIAN && librarian.getUserType() != UserType.ADMIN) {
            throw new IllegalArgumentException("Only a librarian can process book returns");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Check if book is already returned
        if (loan.getStatus() == LoanStatus.RETURNED) {
            return loan;
        }

        // Mark loan as returned
        loan.markAsReturned(librarian);
        loanRepository.saveLoan(loan);

        // Get the book
        Book book = loan.getBook();

        // Check if there are waiting reservations
        boolean hasActiveReservation = reservationService.processReturnedBook(book);

        if (!hasActiveReservation) {
            // No reservations, mark book as available
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.saveBook(book);
        }

        return loan;
    }

    @Override
    @Transactional
    public Loan extendLoan(long loanId, int days, User librarian) {
        // Verify librarian has proper role
        if (librarian.getUserType() != UserType.LIBRARIAN && librarian.getUserType() != UserType.ADMIN) {
            throw new IllegalArgumentException("Only a librarian can extend loans");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Check if loan is active
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Cannot extend a loan that is not active or overdue");
        }

        // Extend due date
        loan.extendLoan();

        return loanRepository.saveLoan(loan);
    }

    @Override
    @Transactional
    public Loan markBookAsLost(long loanId, String notes, User librarian) {
        // Verify librarian has proper role
        if (librarian.getUserType() != UserType.LIBRARIAN && librarian.getUserType() != UserType.ADMIN) {
            throw new IllegalArgumentException("Only a librarian can mark books as lost");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        // Check if this is an active loan
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Only active loans can be marked as lost");
        }

        // Mark loan as lost
        loan.markAsLost(librarian, notes);

        // Update book status
        Book book = loan.getBook();
        book.setStatus(BookStatus.LOST);
        bookRepository.saveBook(book);

        return loanRepository.saveLoan(loan);
    }

    @Override
    @Transactional
    public List<Loan> getAllActiveLoans() {
        return loanRepository.findByStatusIn(List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }

    @Override
    @Transactional
    public List<Loan> getUserLoans(User user) {
        return loanRepository.findByUser(user);
    }

    @Override
    @Transactional
    public List<Loan> getActiveUserLoans(User user) {
        return loanRepository.findByUserAndStatusIn(user, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
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
    public boolean isBookBorrowedByUser(Book book, User user) {
        return loanRepository.existsByBookAndUserAndStatusIn(book, user, List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }
}
