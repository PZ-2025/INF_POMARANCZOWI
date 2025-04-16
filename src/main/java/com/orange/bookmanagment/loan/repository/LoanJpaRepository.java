package com.orange.bookmanagment.loan.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.model.enums.LoanStatus;
import com.orange.bookmanagment.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanJpaRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndUserAndStatusIn(Book book, User user, List<LoanStatus> statuses);

    List<Loan> findByStatusIn(List<LoanStatus> statuses);

    List<Loan> findByUser(User user);

    List<Loan> findByUserAndStatusIn(User user, List<LoanStatus> statuses);


}
