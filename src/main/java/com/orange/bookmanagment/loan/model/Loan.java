package com.orange.bookmanagment.loan.model;

import com.orange.bookmanagment.shared.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Setter
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long bookId;

    private long userId;

    @Enumerated(EnumType.STRING)

    private LoanStatus status;

    private String notes;

    private Long lendingLibrarianId;

    private Instant borrowedAt;

    private Long returningLibrarianId;

    private Instant returnedAt;

    private Instant dueDate;

    private Instant updatedAt;

    private int extendedCount = 0;

    /**
     * Constructor to create a new Loan instance.
     *
     * @param book          The book being loaned.
     * @param user          The user borrowing the book.
     * @param loanStatus    The status of the loan.
     * @param librarian     The librarian processing the loan.
     * @param notes         Additional notes about the loan.
     */
    public Loan(long book, long user, LoanStatus loanStatus, long librarian, String notes) {
        this.bookId = book;
        this.userId = user;
        this.status = loanStatus;
        this.notes = notes;
        this.lendingLibrarianId = librarian;
        this.borrowedAt = Instant.now();
        this.dueDate = Instant.now().plusSeconds(604800); // 7 days in seconds
    }

    /**
     * Marks the loan as returned.
     *
     * @param librarian The librarian processing the return.
     */
    public void markAsReturned(long librarian) {
        this.status = LoanStatus.RETURNED;
        this.returningLibrarianId = librarian;
        this.updatedAt = Instant.now();
        this.returnedAt = Instant.now();
    }

    public void extendLoan() {
        this.dueDate = this.dueDate.plusSeconds(30L * 24 * 60 * 60 ); // month in seconds
        this.updatedAt = Instant.now();
        this.extendedCount++;

        // Update the loan status if needed
        if (this.status == LoanStatus.OVERDUE && dueDate.isAfter(Instant.now())) {
            this.status = LoanStatus.ACTIVE;
        }
    }

    public void markAsLost(long librarian, String notes) {
        this.status = LoanStatus.LOST;
        this.returningLibrarianId = librarian;
        this.notes = notes;
        this.updatedAt = Instant.now();
    }
}
