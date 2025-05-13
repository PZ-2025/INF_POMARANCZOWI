package com.orange.bookmanagment.user.web.exception;

import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.user.security.exception.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Handler wyjątków związanych z bezpieczeństwem i autoryzacją użytkownika.
 * <p>
 * Obsługuje błędy logowania i nieprawidłowe dane uwierzytelniające.
 */
@ControllerAdvice

class SecurityExceptionHandler {

    /**
     * Obsługuje wyjątek {@link InvalidCredentialsException} rzucany w przypadku nieprawidłowych danych logowania.
     *
     * @param e wyjątek zawierający szczegóły błędu autoryzacji
     * @return odpowiedź HTTP 401 (UNAUTHORIZED) z opisem błędu
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<HttpResponse> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return ResponseEntity.status(UNAUTHORIZED).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(UNAUTHORIZED.value())
                        .httpStatus(UNAUTHORIZED)
                        .message(e.getMessage())
                        .reason("Authorization failed")
                        .build()
        );
    }
}
