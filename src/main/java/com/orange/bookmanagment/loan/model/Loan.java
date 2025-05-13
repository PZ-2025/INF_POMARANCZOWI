package com.orange.bookmanagment.loan.model;

import com.orange.bookmanagment.shared.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Encja reprezentująca wypożyczenie książki przez użytkownika.
 * Zawiera informacje o książce, użytkowniku, terminach,
 * statusie oraz bibliotekarzach obsługujących wypożyczenie i zwrot.
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
     * Konstruktor tworzący nowe wypożyczenie.
     *
     * @param book ID książki
     * @param user ID użytkownika
     * @param loanStatus status wypożyczenia
     * @param librarian ID bibliotekarza rejestrującego
     * @param notes notatki wypożyczenia
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
     * Oznacza wypożyczenie jako zwrócone.
     *
     * @param librarian ID bibliotekarza przyjmującego zwrot
     */
    public void markAsReturned(long librarian) {
        this.status = LoanStatus.RETURNED;
        this.returningLibrarianId = librarian;
        this.updatedAt = Instant.now();
        this.returnedAt = Instant.now();
    }

    /**
     * Przedłuża wypożyczenie o 30 dni.
     * Jeśli wypożyczenie było przeterminowane, zmienia jego status na ACTIVE.
     */
    public void extendLoan() {
        this.dueDate = this.dueDate.plusSeconds(30L * 24 * 60 * 60 ); // month in seconds
        this.updatedAt = Instant.now();
        this.extendedCount++;

        // Update the loan status if needed
        if (this.status == LoanStatus.OVERDUE && dueDate.isAfter(Instant.now())) {
            this.status = LoanStatus.ACTIVE;
        }
    }

    /**
     * Oznacza książkę jako zagubioną.
     *
     * @param librarian ID bibliotekarza zgłaszającego zgubienie
     * @param notes dodatkowe informacje o sytuacji
     */
    public void markAsLost(long librarian, String notes) {
        this.status = LoanStatus.LOST;
        this.returningLibrarianId = librarian;
        this.notes = notes;
        this.updatedAt = Instant.now();
    }
}
