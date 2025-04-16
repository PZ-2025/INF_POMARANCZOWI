package com.orange.bookmanagment.loan.service;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.loan.exception.LoanNotFoundException;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.reservation.exception.BookNotAvailableException;
import com.orange.bookmanagment.user.model.User;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    /**
     * <p>Borrow a book (must be done by a librarian)</p>
     *
     * @param book book to borrow
     * @param user user who is borrowing the book
     * @param librarian librarian who is processing the loan
     * @param notes optional notes about the loan
     * @return the created loan
     * @throws BookNotAvailableException if the book is not available
     * @throws IllegalArgumentException if the librarian is not a valid librarian
     */
    Loan borrowBook(Book book, User user, User librarian, String notes) throws BookNotAvailableException, IllegalArgumentException;

    /**
     * <p>Return a book (must be processed by a librarian)</p>
     *
     * @param loanId ID of the loan to return
     * @param librarian librarian who is processing the return
     * @return the updated loan
     * @throws LoanNotFoundException if the loan is not found
     * @throws IllegalArgumentException if the librarian is not a valid librarian
     */
    Loan returnBook(long loanId, User librarian) throws LoanNotFoundException, IllegalArgumentException;

    /**
     * <p>Extend loan period (must be done by a librarian)</p>
     *
     * @param loanId ID of the loan to extend
     * @param days number of days to extend the loan by
     * @param librarian librarian who is approving the extension
     * @return the updated loan
     * @throws LoanNotFoundException if the loan is not found
     * @throws IllegalArgumentException if the librarian is not a valid librarian
     */
    Loan extendLoan(long loanId, int days, User librarian) throws LoanNotFoundException, IllegalArgumentException;

    /**
     * <p>Mark a book as lost (must be done by a librarian)</p>
     *
     * @param loanId ID of the loan to mark as lost
     * @param notes notes about the lost book
     * @param librarian librarian who is processing the lost report
     * @return the updated loan
     * @throws LoanNotFoundException if the loan is not found
     * @throws IllegalArgumentException if the librarian is not a valid librarian
     */
    Loan markBookAsLost(long loanId, String notes, User librarian) throws LoanNotFoundException, IllegalArgumentException;

    /**
     * <p>Update overdue loans status</p>
     * Updates the status of all loans that are past their due date to OVERDUE
     * This method should be called regularly (e.g., by a scheduled task)
     *
     * @return number of loans updated
     */
//    int updateOverdueLoans();

    /**
     * Checks if a book is currently borrowed.
     *
     * @param book the book to check
     * @return true if the book is borrowed, false otherwise
     */
    boolean isBookBorrowedByUser(Book book, User user);


    List<Loan> getAllActiveLoans();

    List<Loan> getUserLoans(User user);

    List<Loan> getActiveUserLoans(User user);

    Loan getLoanById(long loanId) throws LoanNotFoundException;

}
