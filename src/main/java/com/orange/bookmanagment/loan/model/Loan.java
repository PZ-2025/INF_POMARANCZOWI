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

}
