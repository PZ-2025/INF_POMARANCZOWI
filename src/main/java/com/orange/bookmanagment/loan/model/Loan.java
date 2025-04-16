package com.orange.bookmanagment.loan.model;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.loan.model.enums.LoanStatus;
import com.orange.bookmanagment.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents a loan of a book to a user.
 * <p>
 * This entity contains information about the book being loaned, the user who borrowed it,
 * the status of the loan, and the librarians involved in the lending and returning process.
 */
@Entity
@Table(name = "loans")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lending_librarian_id")
    private User lendingLibrarian;
    private Instant borrowedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returning_librarian_id")
    private User returningLibrarian;
    private Instant returnedAt;

    private Instant dueDate;
    private Instant updatedAt;

    /**
     * Constructor for creating a new loan.
     *
     * @param book         The book being loaned.
     * @param user         The user who borrowed the book.
     * @param loanStatus   The status of the loan.
     * @param librarian    The librarian processing the loan.
     * @param notes        Optional notes about the loan.
     */
    public Loan(Book book, User user, LoanStatus loanStatus, User librarian, String notes) {
        this.book = book;
        this.user = user;
        this.status = loanStatus;
        this.notes = notes;
        this.lendingLibrarian = librarian;
        this.borrowedAt = Instant.now();
        this.dueDate = Instant.now().plusSeconds(604800); // 7 days in seconds
    }

    /**
     * Marks the loan as returned.
     *
     * @param librarian The librarian processing the return.
     */
    public void markAsReturned(User librarian) {
        this.status = LoanStatus.RETURNED;
        this.returningLibrarian = librarian;
        this.updatedAt = Instant.now();
        this.returnedAt = Instant.now();
    }

    public void extendLoan() {
        this.dueDate = this.dueDate.plusSeconds(30L * 24 * 60 * 60 ); // month in seconds
        this.updatedAt = Instant.now();

        // Update the loan status if needed
        if (this.status == LoanStatus.OVERDUE && dueDate.isAfter(Instant.now())) {
            this.status = LoanStatus.ACTIVE;
        }
    }

    public void markAsLost(User librarian, String notes) {
        this.status = LoanStatus.LOST;
        this.returningLibrarian = librarian;
        this.notes = notes;
        this.updatedAt = Instant.now();
    }

}
