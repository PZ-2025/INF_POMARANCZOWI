package com.orange.bookmanagment.loan.web.controller;

import com.orange.bookmanagment.loan.model.Loan;
import com.orange.bookmanagment.loan.service.LoanService;
import com.orange.bookmanagment.loan.web.mapper.LoanMapper;
import com.orange.bookmanagment.loan.web.requests.CreateLoanRequest;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.user.api.UserExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserExternalService userExternalService;
    private final LoanMapper loanMapper;

    /**
     * <p>Borrow a book (librarian only)</p>
     * This endpoint is used by librarians to create a new loan for a user.
     * It can also be used to convert a reservation to a loan.
     *
     * @param request loan request (bookId, userId, notes)
     * @param authentication authenticated librarian
     * @return the created loan
     */
    @PostMapping("/borrow")
    public ResponseEntity<HttpResponse> borrowBook(
            @RequestBody CreateLoanRequest request,
            Authentication authentication) {

        final Long bookId = request.bookId();
        final Long userId = request.userId();

        final Long librarianId = userExternalService.getUserIdByEmail(authentication.getName());

        Loan loan = loanService.borrowBook(bookId, userId, librarianId, request.notes());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .httpStatus(HttpStatus.CREATED)
                        .reason("Book borrowed successfully")
                        .message("Book borrowed")
                        .data(Map.of("loan", loanMapper.toDto(loan)))
                        .build());

    }

    /**
     * <p>Return a book (librarian only)</p>
     *
     * @param loanId ID of the loan to return
     * @param authentication authenticated librarian
     * @return the updated loan
     */
    @PostMapping("{loanId}/return")
    public ResponseEntity<HttpResponse> returnBook(
            @PathVariable long loanId,
            Authentication authentication) {

        final long librarianId = userExternalService.getUserIdByEmail(authentication.getName());
        final Loan loan = loanService.returnBook(loanId, librarianId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("Book returned successfully")
                        .message("Book returned")
                        .data(Map.of("loan", loanMapper.toDto(loan)))
                        .build());
    }

    /**
     * <p>Extend loan period (librarian only)</p>
     *
     * @param loanId ID of the loan to extend
     * @param authentication authenticated librarian
     * @return the updated loan
     */
    @PostMapping("{loanId}/extend")
    public ResponseEntity<HttpResponse> extendLoan(
            @PathVariable long loanId,
            Authentication authentication) {

        final long librarianId = userExternalService.getUserIdByEmail(authentication.getName());
        final Loan loan = loanService.extendLoan(loanId, librarianId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("Loan extended successfully")
                        .message("Loan extended")
                        .data(Map.of("loan", loanMapper.toDto(loan)))
                        .build());
    }

    /**
     * <p>Mark a book as lost (librarian only)</p>
     *
     * @param loanId ID of the loan to mark as lost
     * @param notes notes about the lost book
     * @param authentication authenticated librarian
     * @return the updated loan
     */
    @PostMapping("{loanId}/lost")
    public ResponseEntity<HttpResponse> markBookAsLost(
            @PathVariable long loanId,
            @RequestParam String notes,
            Authentication authentication) {

        final long librarianId = userExternalService.getUserIdByEmail(authentication.getName());
        final Loan loan = loanService.markBookAsLost(loanId, notes, librarianId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("Book marked as lost successfully")
                        .message("Book marked as lost")
                        .data(Map.of("loan", loanMapper.toDto(loan)))
                        .build());
    }

    /**
     * <p>Get all active loans (librarian only)</p>
     *
     * @return list of all active loans
     */
    @GetMapping
    public ResponseEntity<HttpResponse> getAllActiveLoans() {

        List<Loan> loans = loanService.getAllActiveLoans();
        return ResponseEntity.status(HttpStatus.OK)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("Loans retrieved successfully")
                        .message("Loans retrieved")
                        .data(Map.of("loans", loans.stream().map(loanMapper::toDto).toList()))
                        .build());
    }

    /**
     * <p>Get user's loans (librarian or self)</p>
     *
     * @param userId ID of the user to get loans for
     * @return list of loans for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<HttpResponse> getUserLoans(
            @PathVariable long userId) {

        final List<Loan> loans = loanService.getUserLoans(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("User loans retrieved successfully")
                        .message("User loans retrieved")
                        .data(Map.of("loans", loans.stream().map(loanMapper::toDto).toList()))
                        .build());
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<HttpResponse> getActiveUserLoans(
            @PathVariable long userId) {

        final List<Loan> loans = loanService.getActiveUserLoans(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("User active loans retrieved successfully")
                        .message("User active loans retrieved")
                        .data(Map.of("loans", loans.stream().map(loanMapper::toDto).toList()))
                        .build());
    }

    /**
     * <p>Get my loans (any user)</p>
     *
     * @param authentication authenticated user
     * @return list of loans for the authenticated user
     */
    @GetMapping("/my")
    public ResponseEntity<HttpResponse> getMyLoans(
            Authentication authentication) {

        final long userId = userExternalService.getUserIdByEmail(authentication.getName());
        final List<Loan> loans = loanService.getUserLoans(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("My loans retrieved successfully")
                        .message("My loans retrieved")
                        .data(Map.of("loans", loans.stream().map(loanMapper::toDto).toList()))
                        .build());
    }

    /**
     * <p>Get loan by ID (librarian or self)</p>
     *
     * @param loanId ID of the loan to get
     * @return the loan
     */
    @GetMapping("/{loanId}")
    public ResponseEntity<HttpResponse> getLoanById(
            @PathVariable long loanId) {

        final Loan loan = loanService.getLoanById(loanId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("Loan retrieved successfully")
                        .message("Loan retrieved")
                        .data(Map.of("loan", loanMapper.toDto(loan)))
                        .build());
    }


}
