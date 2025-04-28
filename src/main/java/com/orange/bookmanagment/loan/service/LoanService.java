package com.orange.bookmanagment.loan.service;


import com.orange.bookmanagment.loan.exception.LoanNotFoundException;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.exception.BookNotAvailableException;

import java.util.List;

public interface LoanService {

    /**
     * <p>Borrow a book (must be done by a librarian)</p>
     *
     * @param bookId ID of the book to borrow
     * @param userId ID of the user who is borrowing the book
     * @param librarianId ID of the librarian who is processing the loan
     * @param notes optional notes about the loan
     * @return the created loan
     * @throws BookNotAvailableException if the book is not available
     * @throws IllegalArgumentException if the librarian is not a valid librarian
     */
    Loan borrowBook(Long bookId, Long userId, Long librarianId, String notes) throws BookNotAvailableException, IllegalArgumentException;

    /**
     * <p>Return a book (must be processed by a librarian)</p>
     *
     * @param loanId ID of the loan to return
     * @param librarianId ID of the librarian who is processing the return
     * @return the updated loan
     * @throws LoanNotFoundException if the loan is not found
     * @throws IllegalArgumentException if the librarian is not a valid librarian
     */
    Loan returnBook(long loanId, Long librarianId) throws LoanNotFoundException, IllegalArgumentException;

    /**
     * <p>Extend loan period (must be done by a librarian)</p>
     *
     * @param loanId ID of the loan to extend
     * @param librarianId ID of the librarian who is approving the extension
     * @return the updated loan
     * @throws LoanNotFoundException if the loan is not found
     * @throws IllegalArgumentException if the librarian is not a valid librarian
     */
    Loan extendLoan(long loanId, long librarianId) throws LoanNotFoundException, IllegalArgumentException;

    /**
     * <p>Mark a book as lost (must be done by a librarian)</p>
     *
     * @param loanId ID of the loan to mark as lost
     * @param notes notes about the lost book
     * @param librarianId ID of the librarian who is processing the lost report
     * @return the updated loan
     * @throws LoanNotFoundException if the loan is not found
     * @throws IllegalArgumentException if the librarian is not a valid librarian
     */
    Loan markBookAsLost(long loanId, String notes, Long librarianId) throws LoanNotFoundException, IllegalArgumentException;

    List<Loan> getAllActiveLoans();

    List<Loan> getUserLoans(long userId);

    List<Loan> getActiveUserLoans(long userId);

    Loan getLoanById(long loanId) throws LoanNotFoundException;

//    /**
//     * <p>Update overdue loans status</p>
//     * Updates the status of all loans that are past their due date to OVERDUE
//     * This method should be called regularly (e.g., by a scheduled task)
//     *
//     * @return number of loans updated
//     */
////    int updateOverdueLoans();
//
//    /**
//     * Checks if a book is currently borrowed.
//     *
//     * @param bookId the ID of the book
//     * @return true if the book is borrowed, false otherwise
//     */
//    boolean isBookBorrowedByUser(Long bookId, Long userId);
//
//

}
