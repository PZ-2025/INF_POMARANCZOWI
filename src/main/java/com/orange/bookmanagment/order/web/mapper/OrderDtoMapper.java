package com.orange.bookmanagment.order.web.mapper;

import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.web.model.OrderDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.stereotype.Component;

/**
 * Mapper odpowiedzialny za konwersję encji {@link Order} na obiekty Data Transfer Object {@link OrderDto}.
 *
 * <p>OrderDtoMapper stanowi warstwę transformacji danych między warstwą domenową a warstwą prezentacji.
 * Głównym celem jest oddzielenie wewnętrznej reprezentacji danych (encji JPA) od danych eksponowanych
 * przez API REST, co zapewnia enkapsulację i niezależność warstw aplikacji.</p>
 *
 * <p>Mapper automatycznie formatuje dane podczas konwersji:
 * <ul>
 * <li>Konwertuje znaczniki czasu {@link java.time.Instant} na czytelne formaty string</li>
 * <li>Zachowuje wszystkie istotne dane zamówienia w formie dostępnej dla warstwy web</li>
 * <li>Zapewnia spójność formatowania danych w całej aplikacji</li>
 * </ul></p>
 *
 * <p>Klasa jest zarządzana przez kontener Spring IoC jako komponent, co umożliwia jej
 * wstrzykiwanie do innych klas aplikacji poprzez dependency injection.</p>
 *
 * @since 1.0
 * @see Order
 * @see OrderDto
 * @see TimeUtil
 * @see Component
 */
@Component
public class OrderDtoMapper {

    /**
     * Konwertuje encję Order na obiekt Data Transfer Object OrderDto.
     *
     * <p>Metoda wykonuje mapowanie wszystkich istotnych pól z encji biznesowej Order
     * na odpowiadające im pola w obiekcie DTO. Podczas konwersji automatycznie
     * formatuje czas zamówienia z {@link java.time.Instant} na czytelny format string
     * za pomocą {@link TimeUtil#getTimeInStandardFormat}.</p>
     *
     * <p>Konwertowane pola:
     * <ul>
     * <li><b>id</b> - unikalny identyfikator zamówienia (bez zmian)</li>
     * <li><b>supplier</b> - nazwa dostawcy (bez zmian)</li>
     * <li><b>orderedBooks</b> - lista zamówionych książek (bez zmian)</li>
     * <li><b>orderPriority</b> - priorytet zamówienia (bez zmian)</li>
     * <li><b>orderStatus</b> - status zamówienia (bez zmian)</li>
     * <li><b>orderTime</b> - czas zamówienia (formatowany na string)</li>
     * </ul></p>
     *
     * <p>Obiekt DTO nie zawiera wrażliwych informacji takich jak czasy aktualizacji
     * czy inne metadane wewnętrzne, co zapewnia bezpieczeństwo i czytelność API.</p>
     *
     * @param order encja zamówienia do konwersji; nie może być null
     * @return obiekt OrderDto zawierający dane zamówienia sformatowane dla warstwy prezentacji
     * @throws NullPointerException jeśli parametr order jest null
     * @throws IllegalStateException jeśli którekolwiek z wymaganych pól encji jest null
     * @see OrderDto#OrderDto(long, String, java.util.List, com.orange.bookmanagment.order.model.enums.OrderPriority, com.orange.bookmanagment.order.model.enums.OrderStatus, String)
     * @see TimeUtil#getTimeInStandardFormat(java.time.Instant)
     */
    public OrderDto toDto(Order order) {
        return new OrderDto(order.getId(),
                order.getSupplier(),
                order.getOrderedBooks(),
                order.getOrderPriority(),
                order.getOrderStatus(),
                TimeUtil.getTimeInStandardFormat(order.getOrderTime()));
    }
}