package com.orange.bookmanagment.loan.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.model.enums.LoanStatus;
import com.orange.bookmanagment.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class LoanRepository {

    private final LoanJpaRepository loanJpaRepository;

    /**
     * Checks if a loan exists for a given book, user, and list of statuses.
     *
     * @param book     the book entity
     * @param user     the user entity
     * @param statuses the list of loan statuses to check
     * @return true if a loan exists with the specified criteria, false otherwise
     */
    public boolean existsByBookAndUserAndStatusIn(Book book, User user, List<LoanStatus> statuses) {
        return loanJpaRepository.existsByBookAndUserAndStatusIn(book, user, statuses);
    }

    /**
     * Saves a loan entity to the database.
     *
     * @param loan the loan entity to save
     * @return the saved loan entity
     */
    public Loan saveLoan(Loan loan) {
        return loanJpaRepository.save(loan);
    }

    /**
     * Finds a loan entity by its ID.
     *
     * @param id the ID of the loan entity to find
     * @return an {@link Optional} containing the found loan entity, or empty if not found
     */
    public Optional<Loan> findById(long id) {
        return loanJpaRepository.findById(id);
    }

    //findByStatusIn
    /**
     * Finds all loan entities with the specified statuses.
     *
     * @param statuses the list of loan statuses to filter by
     * @return a list of loan entities with the specified statuses
     */
    public List<Loan> findByStatusIn(List<LoanStatus> statuses) {
        return loanJpaRepository.findByStatusIn(statuses);
    }

    /**
     * Finds all loan entities associated with a specific user.
     *
     * @param user the user entity to filter by
     * @return a list of loan entities associated with the specified user
     */
    public List<Loan> findByUser(User user) {
        return loanJpaRepository.findByUser(user);
    }

    //findByUserAndStatusIn
    /**
     * Finds all loan entities associated with a specific user and having the specified statuses.
     *
     * @param user     the user entity to filter by
     * @param statuses the list of loan statuses to filter by
     * @return a list of loan entities associated with the specified user and having the specified statuses
     */
    public List<Loan> findByUserAndStatusIn(User user, List<LoanStatus> statuses) {
        return loanJpaRepository.findByUserAndStatusIn(user, statuses);
    }



}
