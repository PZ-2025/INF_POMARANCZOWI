package com.orange.bookmanagment.loan.repository;

import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.shared.enums.LoanStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class LoanRepository {

    private final LoanJpaRepository loanJpaRepository;

    /**
     * Checks if a loan exists for a specific book and user with the given statuses.
     *
     * @param bookId   the ID of the book
     * @param userId   the ID of the user
     * @param statuses  the list of loan statuses to check
     * @return true if a loan exists, false otherwise
     */
    public boolean existsByBookIdAndUserIdAndStatusIn(long bookId, long userId, List<LoanStatus> statuses) {
        return loanJpaRepository.existsByBookIdAndUserIdAndStatusIn(bookId, userId, statuses);
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
     * @param userId the user entity to filter by
     * @return a list of loan entities associated with the specified user
     */
    public List<Loan> findByUserId(long userId) {
        return loanJpaRepository.findByUserId(userId);
    }

    /**
     * Finds all loan entities associated with a specific user and having the specified statuses.
     *
     * @param userId   the ID of the user
     * @param statuses  the list of loan statuses to filter by
     * @return a list of loan entities associated with the specified user and having the specified statuses
     */
    public List<Loan> findByUserAndStatusIn(long userId, List<LoanStatus> statuses) {
        return loanJpaRepository.findByUserIdAndStatusIn(userId, statuses);
    }

    public List<Loan> findAll() {
        return loanJpaRepository.findAll();
    }
}
