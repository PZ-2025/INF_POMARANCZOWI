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

/**
 * Kontroler REST odpowiedzialny za operacje związane z wypożyczeniami książek.
 * <p>
 * Obsługuje proces wypożyczania, zwrotu, przedłużenia, oznaczania jako zagubione
 * oraz pobierania danych o wypożyczeniach (dla bibliotekarzy i użytkowników).
 */
@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserExternalService userExternalService;
    private final LoanMapper loanMapper;

    /**
     * Tworzy nowe wypożyczenie książki.
     * Może również przekształcić rezerwację w wypożyczenie.
     *
     * @param request obiekt żądania wypożyczenia
     * @param authentication uwierzytelniony bibliotekarz
     * @return nowo utworzone wypożyczenie
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
     * Zwraca książkę.
     *
     * @param loanId identyfikator wypożyczenia
     * @param authentication uwierzytelniony bibliotekarz
     * @return zaktualizowane wypożyczenie
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
     * Przedłuża okres wypożyczenia.
     *
     * @param loanId identyfikator wypożyczenia
     * @param authentication uwierzytelniony bibliotekarz
     * @return zaktualizowane wypożyczenie
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
     * Oznacza wypożyczenie jako zagubione.
     *
     * @param loanId identyfikator wypożyczenia
     * @param notes notatki dotyczące zagubienia
     * @param authentication uwierzytelniony bibliotekarz
     * @return zaktualizowane wypożyczenie
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
     * Zwraca listę wszystkich aktywnych wypożyczeń (tylko dla bibliotekarzy).
     *
     * @return lista aktywnych wypożyczeń
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
     * Zwraca wszystkie wypożyczenia danego użytkownika (dla siebie lub przez bibliotekarza).
     *
     * @param userId identyfikator użytkownika
     * @return lista wypożyczeń użytkownika
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

    /**
     * Zwraca wszystkie aktywne wypożyczenia danego użytkownika.
     *
     * @param userId identyfikator użytkownika
     * @return lista aktywnych wypożyczeń użytkownika
     */
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
     * Zwraca wypożyczenia aktualnie zalogowanego użytkownika.
     *
     * @param authentication uwierzytelniony użytkownik
     * @return lista wypożyczeń zalogowanego użytkownika
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
     * Zwraca wypożyczenie na podstawie jego ID.
     *
     * @param loanId identyfikator wypożyczenia
     * @return szczegóły wypożyczenia
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

    /**
     * Obsługuje wyjątek nieprawidłowego stanu operacji związanej z wypożyczeniami.
     *
     * @param ex wyjątek
     * @return odpowiedź z błędem
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<HttpResponse> handleIllegalStateException(IllegalStateException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(HttpResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .reason("Invalid loan operation")
                        .message(ex.getMessage())
                        .build());
    }
}
