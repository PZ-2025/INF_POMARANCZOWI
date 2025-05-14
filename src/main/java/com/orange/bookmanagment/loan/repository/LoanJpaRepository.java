package com.orange.bookmanagment.loan.repository;

import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.shared.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interfejs JPA do operacji na encjach {@link Loan}.
 * Umożliwia wykonywanie zapytań na bazie danych bez implementacji.
 */
@Repository
public interface LoanJpaRepository extends JpaRepository<Loan, Long> {

    /**
     * Sprawdza, czy istnieje wypożyczenie o danym bookId, userId i statusie z listy.
     *
     * @param bookId ID książki
     * @param userId ID użytkownika
     * @param statuses lista statusów
     * @return true, jeśli wypożyczenie istnieje
     */
    boolean existsByBookIdAndUserIdAndStatusIn(Long bookId, Long userId, List<LoanStatus> statuses);

    /**
     * Zwraca wypożyczenia o podanych statusach.
     *
     * @param statuses lista statusów
     * @return lista wypożyczeń
     */
    List<Loan> findByStatusIn(List<LoanStatus> statuses);

    /**
     * Zwraca wypożyczenia przypisane do użytkownika.
     *
     * @param userId identyfikator użytkownika
     * @return lista wypożyczeń użytkownika
     */
    List<Loan> findByUserId(Long userId);

    /**
     * Zwraca wypożyczenia użytkownika o podanych statusach.
     *
     * @param userId identyfikator użytkownika
     * @param statuses lista statusów
     * @return lista wypożyczeń użytkownika z danym statusem
     */
    List<Loan> findByUserIdAndStatusIn(Long userId, List<LoanStatus> statuses);

    /**
     * Sprawdza, czy istnieje przynajmniej jedno wypożyczenie dla danej książki
     * o jednym z podanych statusów.
     *
     * @param bookId ID książki
     * @param statuses lista statusów wypożyczenia (np. ACTIVE, OVERDUE)
     * @return true, jeśli istnieje takie wypożyczenie; false w przeciwnym razie
     */
    boolean existsByBookIdAndStatusIn(long bookId, List<LoanStatus> statuses);
}
