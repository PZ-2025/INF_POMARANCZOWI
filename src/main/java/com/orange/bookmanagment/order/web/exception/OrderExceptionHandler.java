package com.orange.bookmanagment.order.web.exception;

import com.orange.bookmanagment.order.exception.InvalidOrderArgumentException;
import com.orange.bookmanagment.order.exception.OrderAlreadyExistException;
import com.orange.bookmanagment.order.exception.OrderNotFoundException;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

/**
 * Globalny handler wyjątków dla operacji związanych z zamówieniami w warstwie web.
 *
 * <p>OrderExceptionHandler służy jako centralne miejsce obsługi wszystkich wyjątków
 * związanych z domeną zamówień w aplikacji. Klasa jest oznaczona adnotacją {@link ControllerAdvice},
 * co sprawia, że Spring automatycznie stosuje jej metody do wszystkich kontrolerów w aplikacji.</p>
 *
 * <p>Handler konwertuje wyjątki biznesowe na ustandaryzowane odpowiedzi HTTP w postaci
 * {@link HttpResponse} z odpowiednimi kodami statusu, komunikatami oraz znacznikami czasu.
 * Zapewnia to spójność w formacie odpowiedzi błędów w całej aplikacji.</p>
 *
 * <p>Obsługiwane wyjątki:
 * <ul>
 * <li>{@link InvalidOrderArgumentException} - błędy walidacji argumentów (400 Bad Request)</li>
 * <li>{@link OrderNotFoundException} - brak zamówienia (404 Not Found)</li>
 * <li>{@link OrderAlreadyExistException} - duplikacja zamówienia (400 Bad Request)</li>
 * </ul></p>
 *
 * @since 1.0
 * @see ControllerAdvice
 * @see ExceptionHandler
 * @see HttpResponse
 * @see InvalidOrderArgumentException
 * @see OrderNotFoundException
 * @see OrderAlreadyExistException
 */
@ControllerAdvice
class OrderExceptionHandler {

    /**
     * Obsługuje wyjątki związane z nieprawidłowymi argumentami zamówienia.
     *
     * <p>Metoda jest wywoływana automatycznie przez Spring gdy zostanie rzucony
     * wyjątek {@link InvalidOrderArgumentException} w dowolnym kontrolerze aplikacji.
     * Konwertuje wyjątek na odpowiedź HTTP z kodem 400 (Bad Request).</p>
     *
     * <p>Typowe scenariusze gdy ten handler jest używany:
     * <ul>
     * <li>Nieprawidłowe wartości priorytetów lub statusów zamówień</li>
     * <li>Błędy walidacji danych biznesowych</li>
     * <li>Nieprawidłowe formaty danych wejściowych</li>
     * <li>Naruszenie reguł biznesowych dotyczących zamówień</li>
     * </ul></p>
     *
     * @param e wyjątek zawierający szczegóły błędu walidacji; nie może być null
     * @return ResponseEntity z kodem 400 (Bad Request) i szczegółami błędu w formacie HttpResponse
     * @see InvalidOrderArgumentException
     */
    @ExceptionHandler(InvalidOrderArgumentException.class)
    public ResponseEntity<HttpResponse> handleInvalidOrderArgumentException(InvalidOrderArgumentException e) {
        return ResponseEntity.status(BAD_REQUEST).body(HttpResponse.builder()
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .reason("Invalid order argument!")
                .message(e.getMessage())
                .httpStatus(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build());
    }

    /**
     * Obsługuje wyjątki związane z nieznalezionymi zamówieniami.
     *
     * <p>Metoda jest wywoływana automatycznie przez Spring gdy zostanie rzucony
     * wyjątek {@link OrderNotFoundException} w dowolnym kontrolerze aplikacji.
     * Konwertuje wyjątek na odpowiedź HTTP z kodem 404 (Not Found).</p>
     *
     * <p>Typowe scenariusze gdy ten handler jest używany:
     * <ul>
     * <li>Próba pobrania zamówienia o nieistniejącym ID</li>
     * <li>Operacje na zamówieniach które zostały usunięte</li>
     * <li>Dostęp do zamówień spoza zakresu uprawnień użytkownika</li>
     * <li>Błędy w linkach lub bookmark'ach do nieistniejących zamówień</li>
     * </ul></p>
     *
     * @param e wyjątek zawierający szczegóły błędu braku zamówienia; nie może być null
     * @return ResponseEntity z kodem 404 (Not Found) i szczegółami błędu w formacie HttpResponse
     * @see OrderNotFoundException
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<HttpResponse> handleOrderNotFoundException(OrderNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(HttpResponse.builder()
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .reason("Order has not been found!")
                .message(e.getMessage())
                .httpStatus(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build());
    }

    /**
     * Obsługuje wyjątki związane z próbą utworzenia duplikatu zamówienia.
     *
     * <p>Metoda jest wywoływana automatycznie przez Spring gdy zostanie rzucony
     * wyjątek {@link OrderAlreadyExistException} w dowolnym kontrolerze aplikacji.
     * Konwertuje wyjątek na odpowiedź HTTP z kodem 400 (Bad Request).</p>
     *
     * <p>Typowe scenariusze gdy ten handler jest używany:
     * <ul>
     * <li>Próba utworzenia zamówienia z tym samym identyfikatorem</li>
     * <li>Duplikacja zamówienia na podstawie unikalnych kryteriów biznesowych</li>
     * <li>Naruszenie ograniczeń unikalności w bazie danych</li>
     * <li>Próba ponownego złożenia tego samego zamówienia</li>
     * </ul></p>
     *
     * @param e wyjątek zawierający szczegóły błędu duplikacji zamówienia; nie może być null
     * @return ResponseEntity z kodem 400 (Bad Request) i szczegółami błędu w formacie HttpResponse
     * @see OrderAlreadyExistException
     */
    @ExceptionHandler(OrderAlreadyExistException.class)
    public ResponseEntity<HttpResponse> handleOrderAlreadyExistException(OrderAlreadyExistException e) {
        return ResponseEntity.status(BAD_REQUEST).body(HttpResponse.builder()
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .reason("Order already exist!")
                .message(e.getMessage())
                .httpStatus(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build());
    }
}