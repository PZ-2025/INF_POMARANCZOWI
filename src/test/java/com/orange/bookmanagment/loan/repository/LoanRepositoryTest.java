package com.orange.bookmanagment.loan.repository;

import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.model.enums.LoanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanRepositoryTest {

    @Mock
    private LoanJpaRepository loanJpaRepository;

    @InjectMocks
    private LoanRepository loanRepository;

    private Loan testLoan;
    private static final Long BOOK_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long LIBRARIAN_ID = 3L;
    private static final Long LOAN_ID = 4L;
    private static final String NOTES = "Test notes";
    private static final List<LoanStatus> ACTIVE_STATUSES = List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE);

    @BeforeEach
    void setUp() {
        testLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.ACTIVE, LIBRARIAN_ID, NOTES);
        testLoan.setId(LOAN_ID);
    }

    @Test
    void existsByBookIdAndUserIdAndStatusIn_shouldDelegateToJpaRepository() {
        // given
        when(loanJpaRepository.existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES))
                .thenReturn(true);

        // when
        boolean result = loanRepository.existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES);

        // then
        assertThat(result).isTrue();
        verify(loanJpaRepository).existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES);
    }

    @Test
    void existsByBookIdAndUserIdAndStatusIn_whenNoLoanExists_shouldReturnFalse() {
        // given
        when(loanJpaRepository.existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES))
                .thenReturn(false);

        // when
        boolean result = loanRepository.existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES);

        // then
        assertThat(result).isFalse();
        verify(loanJpaRepository).existsByBookIdAndUserIdAndStatusIn(BOOK_ID, USER_ID, ACTIVE_STATUSES);
    }

    @Test
    void saveLoan_shouldDelegateToJpaRepository() {
        // given
        when(loanJpaRepository.save(testLoan)).thenReturn(testLoan);

        // when
        Loan result = loanRepository.saveLoan(testLoan);

        // then
        assertThat(result).isEqualTo(testLoan);
        verify(loanJpaRepository).save(testLoan);
    }

    @Test
    void findById_shouldDelegateToJpaRepository() {
        // given
        when(loanJpaRepository.findById(LOAN_ID)).thenReturn(Optional.of(testLoan));

        // when
        Optional<Loan> result = loanRepository.findById(LOAN_ID);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testLoan);
        verify(loanJpaRepository).findById(LOAN_ID);
    }

    @Test
    void findById_whenLoanDoesNotExist_shouldReturnEmpty() {
        // given
        when(loanJpaRepository.findById(LOAN_ID)).thenReturn(Optional.empty());

        // when
        Optional<Loan> result = loanRepository.findById(LOAN_ID);

        // then
        assertThat(result).isEmpty();
        verify(loanJpaRepository).findById(LOAN_ID);
    }

    @Test
    void findByStatusIn_shouldDelegateToJpaRepository() {
        // given
        Loan activeLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.ACTIVE, LIBRARIAN_ID, NOTES);
        Loan overdueLoan = new Loan(BOOK_ID + 1, USER_ID, LoanStatus.OVERDUE, LIBRARIAN_ID, NOTES);
        List<Loan> expectedLoans = Arrays.asList(activeLoan, overdueLoan);

        when(loanJpaRepository.findByStatusIn(ACTIVE_STATUSES)).thenReturn(expectedLoans);

        // when
        List<Loan> result = loanRepository.findByStatusIn(ACTIVE_STATUSES);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedLoans);
        verify(loanJpaRepository).findByStatusIn(ACTIVE_STATUSES);
    }

    @Test
    void findByStatusIn_whenNoLoansWithStatus_shouldReturnEmptyList() {
        // given
        when(loanJpaRepository.findByStatusIn(ACTIVE_STATUSES)).thenReturn(List.of());

        // when
        List<Loan> result = loanRepository.findByStatusIn(ACTIVE_STATUSES);

        // then
        assertThat(result).isEmpty();
        verify(loanJpaRepository).findByStatusIn(ACTIVE_STATUSES);
    }

    @Test
    void findByUserId_shouldDelegateToJpaRepository() {
        // given
        Loan loan1 = new Loan(BOOK_ID, USER_ID, LoanStatus.ACTIVE, LIBRARIAN_ID, NOTES);
        Loan loan2 = new Loan(BOOK_ID + 1, USER_ID, LoanStatus.RETURNED, LIBRARIAN_ID, NOTES);
        List<Loan> expectedLoans = Arrays.asList(loan1, loan2);

        when(loanJpaRepository.findByUserId(USER_ID)).thenReturn(expectedLoans);

        // when
        List<Loan> result = loanRepository.findByUserId(USER_ID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedLoans);
        verify(loanJpaRepository).findByUserId(USER_ID);
    }

    @Test
    void findByUserId_whenUserHasNoLoans_shouldReturnEmptyList() {
        // given
        when(loanJpaRepository.findByUserId(USER_ID)).thenReturn(List.of());

        // when
        List<Loan> result = loanRepository.findByUserId(USER_ID);

        // then
        assertThat(result).isEmpty();
        verify(loanJpaRepository).findByUserId(USER_ID);
    }

    @Test
    void findByUserAndStatusIn_shouldDelegateToJpaRepository() {
        // given
        Loan activeLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.ACTIVE, LIBRARIAN_ID, NOTES);
        Loan overdueLoan = new Loan(BOOK_ID + 1, USER_ID, LoanStatus.OVERDUE, LIBRARIAN_ID, NOTES);
        List<Loan> expectedLoans = Arrays.asList(activeLoan, overdueLoan);

        when(loanJpaRepository.findByUserIdAndStatusIn(USER_ID, ACTIVE_STATUSES)).thenReturn(expectedLoans);

        // when
        List<Loan> result = loanRepository.findByUserAndStatusIn(USER_ID, ACTIVE_STATUSES);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedLoans);
        verify(loanJpaRepository).findByUserIdAndStatusIn(USER_ID, ACTIVE_STATUSES);
    }

    @Test
    void findByUserAndStatusIn_whenNoLoansFound_shouldReturnEmptyList() {
        // given
        when(loanJpaRepository.findByUserIdAndStatusIn(USER_ID, ACTIVE_STATUSES)).thenReturn(List.of());

        // when
        List<Loan> result = loanRepository.findByUserAndStatusIn(USER_ID, ACTIVE_STATUSES);

        // then
        assertThat(result).isEmpty();
        verify(loanJpaRepository).findByUserIdAndStatusIn(USER_ID, ACTIVE_STATUSES);
    }
}