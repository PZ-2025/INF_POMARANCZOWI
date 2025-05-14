package com.orange.bookmanagment.user.web.exception;

import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import com.orange.bookmanagment.user.exception.IllegalAccountAccessException;
import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

/**
 * Globalny handler wyjątków związanych z operacjami na użytkownikach.
 * <p>
 * Obsługuje następujące wyjątki domenowe:
 * <ul>
 *     <li>{@link com.orange.bookmanagment.user.exception.UserNotFoundException}</li>
 *     <li>{@link com.orange.bookmanagment.user.exception.UserAlreadyExistException}</li>
 *     <li>{@link com.orange.bookmanagment.user.exception.IllegalAccountAccessException}</li>
 * </ul>
 */
@ControllerAdvice
class UserExceptionHandler {

    /**
     * Obsługuje wyjątek rzucany w przypadku, gdy użytkownik nie został znaleziony.
     *
     * @param e wyjątek {@code UserNotFoundException}
     * @return odpowiedź HTTP 404 (NOT_FOUND) z informacją o błędzie
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(NOT_FOUND.value())
                        .httpStatus(NOT_FOUND)
                        .message(e.getMessage())
                        .reason("User not found")
                        .build()
        );
    }

    /**
     * Obsługuje wyjątek rzucany w przypadku próby rejestracji istniejącego już użytkownika.
     *
     * @param e wyjątek {@code UserAlreadyExistException}
     * @return odpowiedź HTTP 400 (BAD_REQUEST) z informacją o błędzie
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<HttpResponse> handleUserAlreadyExistException(UserAlreadyExistException e) {
        return ResponseEntity.status(BAD_REQUEST).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(BAD_REQUEST.value())
                        .httpStatus(BAD_REQUEST)
                        .message(e.getMessage())
                        .reason("User already exist")
                        .build()
        );
    }

    /**
     * Obsługuje wyjątek rzucany w przypadku nieautoryzowanego dostępu do konta.
     *
     * @param e wyjątek {@code IllegalAccountAccessException}
     * @return odpowiedź HTTP 403 (FORBIDDEN) z informacją o błędzie
     */
    @ExceptionHandler(IllegalAccountAccessException.class)
    public ResponseEntity<HttpResponse> handleIllegalAccountAccessException(IllegalAccountAccessException e) {
        return ResponseEntity.status(FORBIDDEN).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(FORBIDDEN.value())
                        .httpStatus(FORBIDDEN)
                        .message(e.getMessage())
                        .reason("Unauthorized access to account")
                        .build()
        );
    }
}
