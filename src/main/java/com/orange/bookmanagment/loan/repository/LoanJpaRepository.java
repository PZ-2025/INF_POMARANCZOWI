package com.orange.bookmanagment.loan.repository;

import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.shared.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanJpaRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookIdAndUserIdAndStatusIn(Long bookId, Long userId, List<LoanStatus> statuses);

    List<Loan> findByStatusIn(List<LoanStatus> statuses);

    List<Loan> findByUserId(Long userId);

    List<Loan> findByUserIdAndStatusIn(Long userId, List<LoanStatus> statuses);
}
