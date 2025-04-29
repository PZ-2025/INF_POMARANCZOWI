package com.orange.bookmanagment.loan.service.impl;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.book.api.BookInternalDto;
import com.orange.bookmanagment.loan.exception.BookNotAvailableException;
import com.orange.bookmanagment.loan.exception.LoanNotFoundException;
import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.model.enums.LoanStatus;
import com.orange.bookmanagment.loan.repository.LoanRepository;
import com.orange.bookmanagment.reservation.api.ReservationExternalService;
import com.orange.bookmanagment.shared.enums.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookExternalService bookExternalService;

    @Mock
    private ReservationExternalService reservationExternalService;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Captor
    private ArgumentCaptor<Loan> loanCaptor;

    private static final Long BOOK_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long LIBRARIAN_ID = 3L;
    private static final Long LOAN_ID = 4L;
    private static final String NOTES = "Test notes";

    private Loan testLoan;
    private BookInternalDto testBook;

    @BeforeEach
    void setUp() {
        testLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.ACTIVE, LIBRARIAN_ID, NOTES);
        testLoan.setId(LOAN_ID);

        testBook = new BookInternalDto(
                BOOK_ID,
                "Test Book",
                List.of(),
                null,
                "Test book description",
                "Fiction",
                BookStatus.AVAILABLE,
                null,
                null,
                null
        );
    }

    @Test
    void borrowBook_whenBookIsAvailable_shouldCreateLoan() {
        // given
        when(bookExternalService.getBookForExternal(BOOK_ID)).thenReturn(testBook);
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(testLoan);

        // when
        Loan result = loanService.borrowBook(BOOK_ID, USER_ID, LIBRARIAN_ID, NOTES);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(BOOK_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);

        verify(bookExternalService).getBookForExternal(BOOK_ID);
        verify(bookExternalService).updateBookStatus(BOOK_ID, BookStatus.BORROWED);
        verify(loanRepository).saveLoan(any(Loan.class));
        verify(reservationExternalService, never()).isBookReservedForUser(anyLong(), anyLong());
    }

    @Test
    void borrowBook_whenBookIsReservedForUser_shouldCreateLoanAndCompleteReservation() {
        // given
        BookInternalDto reservedBook = new BookInternalDto(
                BOOK_ID,
                "Test Book",
                List.of(),
                null,
                "Test book description",
                "Fiction",
                BookStatus.RESERVED,
                null,
                null,
                null
        );

        when(bookExternalService.getBookForExternal(BOOK_ID)).thenReturn(reservedBook);
        when(reservationExternalService.isBookReservedForUser(BOOK_ID, USER_ID)).thenReturn(true);
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(testLoan);

        // when
        Loan result = loanService.borrowBook(BOOK_ID, USER_ID, LIBRARIAN_ID, NOTES);

        // then
        assertThat(result).isNotNull();

        verify(bookExternalService).getBookForExternal(BOOK_ID);
        verify(reservationExternalService).isBookReservedForUser(BOOK_ID, USER_ID);
        verify(reservationExternalService).completeReservation(BOOK_ID, USER_ID);
        verify(bookExternalService).updateBookStatus(BOOK_ID, BookStatus.BORROWED);
        verify(loanRepository).saveLoan(any(Loan.class));
    }

    @Test
    void borrowBook_whenBookIsReservedForAnotherUser_shouldThrowException() {
        // given
        BookInternalDto reservedBook = new BookInternalDto(
                BOOK_ID,
                "Test Book",
                List.of(),
                null,
                "Test book description",
                "Fiction",
                BookStatus.RESERVED,
                null,
                null,
                null
        );

        when(bookExternalService.getBookForExternal(BOOK_ID)).thenReturn(reservedBook);
        when(reservationExternalService.isBookReservedForUser(BOOK_ID, USER_ID)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> loanService.borrowBook(BOOK_ID, USER_ID, LIBRARIAN_ID, NOTES))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessage("Book is reserved for another user");

        verify(bookExternalService).getBookForExternal(BOOK_ID);
        verify(reservationExternalService).isBookReservedForUser(BOOK_ID, USER_ID);
        verify(reservationExternalService, never()).completeReservation(anyLong(), anyLong());
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(loanRepository, never()).saveLoan(any(Loan.class));
    }

    @Test
    void borrowBook_whenBookIsAlreadyBorrowed_shouldThrowException() {
        // given
        BookInternalDto borrowedBook = new BookInternalDto(
                BOOK_ID,
                "Test Book",
                List.of(),
                null,
                "Test book description",
                "Fiction",
                BookStatus.BORROWED,
                null,
                null,
                null
        );

        when(bookExternalService.getBookForExternal(BOOK_ID)).thenReturn(borrowedBook);

        // when/then
        assertThatThrownBy(() -> loanService.borrowBook(BOOK_ID, USER_ID, LIBRARIAN_ID, NOTES))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessage("Book is already borrowed");

        verify(bookExternalService).getBookForExternal(BOOK_ID);
        verify(reservationExternalService, never()).isBookReservedForUser(anyLong(), anyLong());
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(loanRepository, never()).saveLoan(any(Loan.class));
    }

    @Test
    void returnBook_whenLoanExistsAndActive_shouldReturnBookAndUpdateStatus() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(testLoan));
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(testLoan);
        when(reservationExternalService.processReturnedBook(BOOK_ID)).thenReturn(false);

        // when
        Loan result = loanService.returnBook(LOAN_ID, LIBRARIAN_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);

        verify(loanRepository).findById(LOAN_ID);
        verify(reservationExternalService).processReturnedBook(BOOK_ID);
        verify(bookExternalService).updateBookStatus(BOOK_ID, BookStatus.AVAILABLE);
        verify(loanRepository).saveLoan(any(Loan.class));
    }

    @Test
    void returnBook_whenLoanExistsAndActiveAndHasReservation_shouldReturnBookAndNotUpdateStatus() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(testLoan));
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(testLoan);
        when(reservationExternalService.processReturnedBook(BOOK_ID)).thenReturn(true);

        // when
        Loan result = loanService.returnBook(LOAN_ID, LIBRARIAN_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);

        verify(loanRepository).findById(LOAN_ID);
        verify(reservationExternalService).processReturnedBook(BOOK_ID);
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(loanRepository).saveLoan(loanCaptor.capture());

        Loan capturedLoan = loanCaptor.getValue();
        assertThat(capturedLoan.getStatus()).isEqualTo(LoanStatus.RETURNED);
    }

    @Test
    void returnBook_whenLoanIsAlreadyReturned_shouldReturnExistingLoan() {
        // given
        Loan returnedLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.RETURNED, LIBRARIAN_ID, NOTES);
        returnedLoan.setId(LOAN_ID);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(returnedLoan));

        // when
        Loan result = loanService.returnBook(LOAN_ID, LIBRARIAN_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);

        verify(loanRepository).findById(LOAN_ID);
        verify(reservationExternalService, never()).processReturnedBook(anyLong());
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(loanRepository, never()).saveLoan(any(Loan.class));
    }

    @Test
    void returnBook_whenLoanDoesNotExist_shouldThrowException() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> loanService.returnBook(LOAN_ID, LIBRARIAN_ID))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessage("Loan not found with ID: " + LOAN_ID);

        verify(loanRepository).findById(LOAN_ID);
        verify(reservationExternalService, never()).processReturnedBook(anyLong());
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(loanRepository, never()).saveLoan(any(Loan.class));
    }

    @Test
    void extendLoan_whenLoanIsActive_shouldExtendAndSave() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(testLoan));
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(testLoan);

        // when
        Loan result = loanService.extendLoan(LOAN_ID, LIBRARIAN_ID);

        // then
        assertThat(result).isNotNull();

        verify(loanRepository).findById(LOAN_ID);
        verify(loanRepository).saveLoan(any(Loan.class));
    }

    @Test
    void extendLoan_whenLoanIsOverdue_shouldExtendAndSave() {
        // given
        Loan overdueLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.OVERDUE, LIBRARIAN_ID, NOTES);
        overdueLoan.setId(LOAN_ID);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(overdueLoan));
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(overdueLoan);

        // when
        Loan result = loanService.extendLoan(LOAN_ID, LIBRARIAN_ID);

        // then
        assertThat(result).isNotNull();

        verify(loanRepository).findById(LOAN_ID);
        verify(loanRepository).saveLoan(any(Loan.class));
    }

    @Test
    void extendLoan_whenLoanIsReturned_shouldThrowException() {
        // given
        Loan returnedLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.RETURNED, LIBRARIAN_ID, NOTES);
        returnedLoan.setId(LOAN_ID);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(returnedLoan));

        // when/then
        assertThatThrownBy(() -> loanService.extendLoan(LOAN_ID, LIBRARIAN_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot extend a loan that is not active or overdue");

        verify(loanRepository).findById(LOAN_ID);
        verify(loanRepository, never()).saveLoan(any(Loan.class));
    }

    @Test
    void extendLoan_whenLoanDoesNotExist_shouldThrowException() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> loanService.extendLoan(LOAN_ID, LIBRARIAN_ID))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessage("Loan not found with ID: " + LOAN_ID);

        verify(loanRepository).findById(LOAN_ID);
        verify(loanRepository, never()).saveLoan(any(Loan.class));
    }

    @Test
    void markBookAsLost_whenLoanIsActive_shouldMarkAsLostAndUpdateBookStatus() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(testLoan));
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(testLoan);

        // when
        Loan result = loanService.markBookAsLost(LOAN_ID, "Lost book notes", LIBRARIAN_ID);

        // then
        assertThat(result).isNotNull();

        verify(loanRepository).findById(LOAN_ID);
        verify(bookExternalService).updateBookStatus(BOOK_ID, BookStatus.LOST);
        verify(loanRepository).saveLoan(loanCaptor.capture());

        Loan capturedLoan = loanCaptor.getValue();
        assertThat(capturedLoan.getStatus()).isEqualTo(LoanStatus.LOST);
    }

    @Test
    void markBookAsLost_whenLoanIsOverdue_shouldMarkAsLostAndUpdateBookStatus() {
        // given
        Loan overdueLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.OVERDUE, LIBRARIAN_ID, NOTES);
        overdueLoan.setId(LOAN_ID);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(overdueLoan));
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(overdueLoan);

        // when
        Loan result = loanService.markBookAsLost(LOAN_ID, "Lost book notes", LIBRARIAN_ID);

        // then
        assertThat(result).isNotNull();

        verify(loanRepository).findById(LOAN_ID);
        verify(bookExternalService).updateBookStatus(BOOK_ID, BookStatus.LOST);
        verify(loanRepository).saveLoan(loanCaptor.capture());

        Loan capturedLoan = loanCaptor.getValue();
        assertThat(capturedLoan.getStatus()).isEqualTo(LoanStatus.LOST);
    }

    @Test
    void markBookAsLost_whenLoanIsReturned_shouldThrowException() {
        // given
        Loan returnedLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.RETURNED, LIBRARIAN_ID, NOTES);
        returnedLoan.setId(LOAN_ID);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(returnedLoan));

        // when/then
        assertThatThrownBy(() -> loanService.markBookAsLost(LOAN_ID, "Lost book notes", LIBRARIAN_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only active loans can be marked as lost");

        verify(loanRepository).findById(LOAN_ID);
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(loanRepository, never()).saveLoan(any(Loan.class));
    }

    @Test
    void markBookAsLost_whenLoanDoesNotExist_shouldThrowException() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> loanService.markBookAsLost(LOAN_ID, "Lost book notes", LIBRARIAN_ID))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessage("Loan not found with ID: " + LOAN_ID);

        verify(loanRepository).findById(LOAN_ID);
        verify(bookExternalService, never()).updateBookStatus(anyLong(), any(BookStatus.class));
        verify(loanRepository, never()).saveLoan(any(Loan.class));
    }

    @Test
    void getAllActiveLoans_shouldReturnActiveAndOverdueLoans() {
        // given
        Loan activeLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.ACTIVE, LIBRARIAN_ID, NOTES);
        Loan overdueLoan = new Loan(BOOK_ID + 1, USER_ID, LoanStatus.OVERDUE, LIBRARIAN_ID, NOTES);
        List<Loan> expectedLoans = Arrays.asList(activeLoan, overdueLoan);

        when(loanRepository.findByStatusIn(List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE))).thenReturn(expectedLoans);

        // when
        List<Loan> result = loanService.getAllActiveLoans();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedLoans);

        verify(loanRepository).findByStatusIn(List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE));
    }

    @Test
    void getUserLoans_shouldReturnAllUserLoans() {
        // given
        Loan activeLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.ACTIVE, LIBRARIAN_ID, NOTES);
        Loan returnedLoan = new Loan(BOOK_ID + 1, USER_ID, LoanStatus.RETURNED, LIBRARIAN_ID, NOTES);
        List<Loan> expectedLoans = Arrays.asList(activeLoan, returnedLoan);

        when(loanRepository.findByUserId(USER_ID)).thenReturn(expectedLoans);

        // when
        List<Loan> result = loanService.getUserLoans(USER_ID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedLoans);

        verify(loanRepository).findByUserId(USER_ID);
    }

    @Test
    void getActiveUserLoans_shouldReturnActiveAndOverdueLoansForUser() {
        // given
        Loan activeLoan = new Loan(BOOK_ID, USER_ID, LoanStatus.ACTIVE, LIBRARIAN_ID, NOTES);
        Loan overdueLoan = new Loan(BOOK_ID + 1, USER_ID, LoanStatus.OVERDUE, LIBRARIAN_ID, NOTES);
        List<Loan> expectedLoans = Arrays.asList(activeLoan, overdueLoan);

        when(loanRepository.findByUserAndStatusIn(eq(USER_ID), any())).thenReturn(expectedLoans);

        // when
        List<Loan> result = loanService.getActiveUserLoans(USER_ID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedLoans);

        verify(loanRepository).findByUserAndStatusIn(eq(USER_ID), eq(List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE)));
    }

    @Test
    void getLoanById_whenLoanExists_shouldReturnLoan() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(testLoan));

        // when
        Loan result = loanService.getLoanById(LOAN_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(LOAN_ID);

        verify(loanRepository).findById(LOAN_ID);
    }

    @Test
    void getLoanById_whenLoanDoesNotExist_shouldThrowException() {
        // given
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> loanService.getLoanById(LOAN_ID))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessage("Loan not found with ID: " + LOAN_ID);

        verify(loanRepository).findById(LOAN_ID);
    }
}