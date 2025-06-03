package com.orange.bookmanagment.order.web.controller;

import com.orange.bookmanagment.order.exception.InvalidOrderArgumentException;
import com.orange.bookmanagment.order.exception.OrderNotFoundException;
import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.service.OrderService;
import com.orange.bookmanagment.order.web.mapper.OrderDtoMapper;
import com.orange.bookmanagment.order.web.model.OrderDto;
import com.orange.bookmanagment.order.web.requests.OrderCreateRequest;
import com.orange.bookmanagment.order.web.requests.OrderPriorityUpdateRequest;
import com.orange.bookmanagment.order.web.requests.OrderStatusUpdateRequest;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * Kontroler REST API odpowiedzialny za obsługę żądań HTTP związanych z zamówieniami książek.
 *
 * <p>OrderController stanowi warstwę prezentacji w architekturze MVC, eksponując publiczne
 * API dla operacji na zamówieniach. Kontroler obsługuje pełny cykl życia zamówienia:
 * tworzenie, pobieranie, aktualizację oraz finalizację.</p>
 *
 * <p>Wszystkie endpointy zwracają ustandaryzowane odpowiedzi HTTP w postaci {@link HttpResponse}
 * z odpowiednimi kodami statusu, komunikatami oraz danymi. Kontroler automatycznie konwertuje
 * encje {@link Order} na obiekty DTO {@link OrderDto} za pomocą mappera.</p>
 *
 * <p>API obsługuje walidację danych wejściowych na poziomie parametrów i ciała żądania,
 * stronicowanie wyników oraz filtrowanie zamówień według różnych kryteriów.</p>
 *
 * <p>Bazowa ścieżka API: {@code /api/v1/order}</p>
 *
 * @since 1.0
 * @see OrderService
 * @see OrderDto
 * @see OrderDtoMapper
 * @see HttpResponse
 * @see OrderCreateRequest
 * @see OrderPriorityUpdateRequest
 * @see OrderStatusUpdateRequest
 */
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    /**
     * Serwis zawierający logikę biznesową związaną z zamówieniami.
     */
    private final OrderService orderService;

    /**
     * Mapper do konwersji między encjami Order a obiektami DTO.
     */
    private final OrderDtoMapper orderDtoMapper;

    /**
     * Tworzy nowe zamówienie w systemie.
     *
     * <p>Endpoint przyjmuje dane nowego zamówienia, waliduje je, tworzy zamówienie
     * za pomocą serwisu biznesowego, a następnie zwraca szczegóły utworzonego zamówienia.</p>
     *
     * <p><b>HTTP Method:</b> POST<br>
     * <b>Path:</b> {@code /api/v1/order/place}<br>
     * <b>Content-Type:</b> application/json</p>
     *
     * @param orderCreateRequest obiekt zawierający dane nowego zamówienia; jest walidowany automatycznie
     * @return ResponseEntity z kodem 201 (CREATED) i szczegółami utworzonego zamówienia
     * @throws InvalidOrderArgumentException jeśli dane zamówienia są nieprawidłowe (obsługiwane przez @ExceptionHandler)
     * @throws jakarta.validation.ConstraintViolationException jeśli walidacja danych się nie powiedzie
     */
    @PostMapping("/place")
    public ResponseEntity<HttpResponse> placeOrder(@Valid @RequestBody OrderCreateRequest orderCreateRequest) {

        final OrderDto order = orderDtoMapper.toDto(orderService.createOrder(orderCreateRequest));

        return ResponseEntity.status(CREATED).body(HttpResponse.builder()
                .httpStatus(CREATED)
                .statusCode(CREATED.value())
                .reason("Place order request")
                .message("Order has been placed")
                .data(Map.of("order", order))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    /**
     * Pobiera szczegóły zamówienia na podstawie identyfikatora.
     *
     * <p><b>HTTP Method:</b> GET<br>
     * <b>Path:</b> {@code /api/v1/order/{id}}</p>
     *
     * @param id identyfikator zamówienia do pobrania; musi być liczbą dodatnią
     * @return ResponseEntity z kodem 200 (OK) i szczegółami zamówienia
     * @throws OrderNotFoundException jeśli zamówienie o podanym ID nie istnieje (obsługiwane przez @ExceptionHandler)
     */
    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> findOrderById(@PathVariable("id") Long id) {

        final OrderDto orderDto = orderDtoMapper.toDto(orderService.getOrderById(id));

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Order has been found")
                .message("Order by id")
                .data(Map.of("order", orderDto))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    /**
     * Pobiera stronicowaną listę wszystkich zamówień.
     *
     * <p>Endpoint obsługuje stronicowanie wyników dla efektywnego przetwarzania
     * dużych zbiorów zamówień. Parametry page i size są walidowane.</p>
     *
     * <p><b>HTTP Method:</b> GET<br>
     * <b>Path:</b> {@code /api/v1/order/all}<br>
     * <b>Query Parameters:</b> page (int >= 0), size (int >= 0)</p>
     *
     * @param page numer strony do pobrania (numeracja od 0); musi być >= 0
     * @param size liczba elementów na stronie; musi być >= 0
     * @return ResponseEntity z kodem 200 (OK) i listą zamówień dla podanej strony
     * @throws jakarta.validation.ConstraintViolationException jeśli parametry stronicowania są nieprawidłowe
     */
    @GetMapping("/all")
    public ResponseEntity<HttpResponse> findAllOrders(
            @RequestParam @Valid @Min(value = 0, message = "Numer strony musi być wiekszy niż 0") int page,
            @RequestParam @Valid @Min(value = 0, message = "Rozmiar strony musi być większy niż 0") int size) {

        final List<OrderDto> orders = orderService.findOrders(page,size).stream().map(orderDtoMapper::toDto).toList();

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Orders request")
                .message("Orders found")
                .data(Map.of("orders", orders))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    /**
     * Pobiera wszystkie zamówienia o określonym priorytecie.
     *
     * <p><b>HTTP Method:</b> GET<br>
     * <b>Path:</b> {@code /api/v1/order/priority/{priority}}</p>
     *
     * @param priority priorytet zamówień do wyszukania (np. "HIGH", "MEDIUM", "LOW"); nie może być null
     * @return ResponseEntity z kodem 200 (OK) i listą zamówień o podanym priorytecie
     * @throws InvalidOrderArgumentException jeśli podany priorytet nie jest prawidłową wartością (obsługiwane przez @ExceptionHandler)
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<HttpResponse> findOrdersByPriority(@PathVariable @Valid @NotNull String priority) {

        final List<OrderDto> orders = orderService.getOrdersByOrderPriority(priority).stream().map(orderDtoMapper::toDto).toList();

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Orders request")
                .message("Orders found")
                .data(Map.of("orders", orders))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    /**
     * Pobiera wszystkie zamówienia o określonym statusie.
     *
     * <p><b>HTTP Method:</b> GET<br>
     * <b>Path:</b> {@code /api/v1/order/status/{status}}</p>
     *
     * @param status status zamówień do wyszukania (np. "PLACED", "PROCESSING", "COMPLETED"); nie może być null
     * @return ResponseEntity z kodem 200 (OK) i listą zamówień o podanym statusie
     * @throws InvalidOrderArgumentException jeśli podany status nie jest prawidłową wartością (obsługiwane przez @ExceptionHandler)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<HttpResponse> findOrdersByStatus(@PathVariable @Valid @NotNull String status) {

        final List<OrderDto> orders = orderService.getOrdersByOrderStatus(status).stream().map(orderDtoMapper::toDto).toList();

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Orders request")
                .message("Orders found")
                .data(Map.of("orders", orders))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    /**
     * Finalizuje zamówienie zmieniając jego status na końcowy.
     *
     * <p>Endpoint oznacza zamówienie jako zakończone, co zazwyczaj oznacza
     * zmianę statusu na COMPLETED lub podobny stan końcowy.</p>
     *
     * <p><b>HTTP Method:</b> POST<br>
     * <b>Path:</b> {@code /api/v1/order/finish/{id}}</p>
     *
     * @param id identyfikator zamówienia do finalizacji; musi być liczbą >= 0
     * @return ResponseEntity z kodem 200 (OK) i szczegółami sfinalizowanego zamówienia
     * @throws OrderNotFoundException jeśli zamówienie o podanym ID nie istnieje (obsługiwane przez @ExceptionHandler)
     */
    @PostMapping("/finish/{id}")
    public ResponseEntity<HttpResponse> finishOrder(
            @PathVariable @Valid @NotNull @Min(value = 0, message = "Id zamówienia musi być wieksze niż 0") int id) {

        final OrderDto orderDto = orderDtoMapper.toDto(orderService.finishOrder(id));

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .httpStatus(OK)
                .statusCode(OK.value())
                .data(Map.of("order", orderDto))
                .message("Order has been finished")
                .reason("Order finish request")
                .build());
    }

    /**
     * Aktualizuje priorytet zamówienia.
     *
     * <p>Endpoint przyjmuje obiekt zawierający ID zamówienia i nowy priorytet,
     * waliduje dane, a następnie aktualizuje priorytet za pomocą serwisu biznesowego.</p>
     *
     * <p><b>HTTP Method:</b> PATCH<br>
     * <b>Path:</b> {@code /api/v1/order/priority}<br>
     * <b>Content-Type:</b> application/json</p>
     *
     * @param orderPriorityUpdateRequest obiekt zawierający ID zamówienia i nowy priorytet; jest walidowany automatycznie
     * @return ResponseEntity z kodem 200 (OK) i szczegółami zaktualizowanego zamówienia
     * @throws InvalidOrderArgumentException jeśli nowy priorytet jest nieprawidłowy (obsługiwane przez @ExceptionHandler)
     * @throws OrderNotFoundException jeśli zamówienie o podanym ID nie istnieje (obsługiwane przez @ExceptionHandler)
     */
    @PatchMapping("/priority")
    public ResponseEntity<HttpResponse> updateOrderPriority(
            @Valid @RequestBody OrderPriorityUpdateRequest orderPriorityUpdateRequest) {

        final OrderDto orderDto = orderDtoMapper.toDto(orderService.updateOrderPriority(orderPriorityUpdateRequest));

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Order priority update")
                .message("Order has been updated")
                .data(Map.of("order", orderDto))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    /**
     * Aktualizuje status zamówienia.
     *
     * <p>Endpoint przyjmuje obiekt zawierający ID zamówienia i nowy status,
     * waliduje dane, a następnie aktualizuje status za pomocą serwisu biznesowego.</p>
     *
     * <p><b>HTTP Method:</b> PATCH<br>
     * <b>Path:</b> {@code /api/v1/order/status}<br>
     * <b>Content-Type:</b> application/json</p>
     *
     * @param orderStatusUpdateRequest obiekt zawierający ID zamówienia i nowy status; jest walidowany automatycznie
     * @return ResponseEntity z kodem 200 (OK) i szczegółami zaktualizowanego zamówienia
     * @throws InvalidOrderArgumentException jeśli nowy status jest nieprawidłowy (obsługiwane przez @ExceptionHandler)
     * @throws OrderNotFoundException jeśli zamówienie o podanym ID nie istnieje (obsługiwane przez @ExceptionHandler)
     */
    @PatchMapping("/status")
    public ResponseEntity<HttpResponse> updateOrderStatus(
            @Valid @RequestBody OrderStatusUpdateRequest orderStatusUpdateRequest) {

        final OrderDto orderDto = orderDtoMapper.toDto(orderService.updateOrderStatus(orderStatusUpdateRequest));

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Order status update")
                .message("Order has been updated")
                .data(Map.of("order", orderDto))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    /**
     * Pobiera dane pomocnicze dla zamówień (dostępne priorytety lub statusy).
     *
     * <p>Endpoint zwraca zbiory dostępnych wartości dla priorytetów lub statusów zamówień
     * w zależności od parametru type. Przydatne do wypełniania list rozwijanych w UI.</p>
     *
     * <p><b>HTTP Method:</b> GET<br>
     * <b>Path:</b> {@code /api/v1/order/data/{type}}<br>
     * <b>Dostępne typy:</b> "priorities", "status"</p>
     *
     * @param type typ danych do pobrania - "priorities" dla priorytetów lub "status" dla statusów; nie może być null
     * @return ResponseEntity z kodem 200 (OK) i zbiorem dostępnych wartości dla podanego typu,
     *         lub kodem 400 (BAD_REQUEST) jeśli typ nie jest obsługiwany
     */
    @GetMapping("/data/{type}")
    public ResponseEntity<HttpResponse> getOrderData(@PathVariable @Valid @NotNull String type) {
        return switch (type) {
            case "priorities" -> ResponseEntity.status(OK).body(HttpResponse.builder()
                    .httpStatus(OK)
                    .statusCode(OK.value())
                    .reason("Order type data request")
                    .message("Order priorities")
                    .data(Map.of("priorities", orderService.getOrderPriorities()))
                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                    .build());
            case "status" -> ResponseEntity.status(OK).body(HttpResponse.builder()
                    .httpStatus(OK)
                    .statusCode(OK.value())
                    .reason("Order type data request")
                    .message("Order statues")
                    .data(Map.of("priorities", orderService.getOrderStatuses()))
                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                    .build());
            default -> ResponseEntity.status(BAD_REQUEST).body(HttpResponse.builder()
                    .httpStatus(BAD_REQUEST)
                    .statusCode(BAD_REQUEST.value())
                    .reason("Order type data request")
                    .message("Order data not found")
                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                    .build());
        };
    }
}