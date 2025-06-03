package com.orange.bookmanagment.order.repository;

import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repozytorium odpowiedzialne za operacje dostępu do danych związanych z zamówieniami.
 *
 * <p>Klasa OrderRepository służy jako warstwa abstrakcji między warstwą biznesową a warstwą
 * dostępu do danych. Enkapsuluje wszystkie operacje CRUD oraz zapytania specjalistyczne
 * dla encji {@link Order}.</p>
 *
 * <p>Repozytorium deleguje operacje do {@link OrderJpaRepository}, które implementuje
 * standardowe interfejsy Spring Data JPA. Dostarcza metody do wyszukiwania zamówień
 * według różnych kryteriów takich jak status, priorytet, dostawca, data itp.</p>
 *
 * <p>Klasa obsługuje także stronicowanie wyników dla lepszej wydajności przy dużych
 * zbiorach danych.</p>
 *
 * @since 1.0
 * @see Order
 * @see OrderJpaRepository
 * @see OrderPriority
 * @see OrderStatus
 */
@Repository
@RequiredArgsConstructor
public class OrderRepository {

    /**
     * Interfejs JPA Repository dla operacji bazodanowych na encji Order.
     *
     * <p>Wstrzykiwany przez konstruktor za pomocą mechanizmu dependency injection.</p>
     */
    private final OrderJpaRepository orderJpaRepository;

    /**
     * Zapisuje zamówienie w bazie danych.
     *
     * <p>Jeśli zamówienie ma już przypisane ID, wykonuje operację aktualizacji.
     * W przeciwnym przypadku tworzy nowy rekord w bazie danych.</p>
     *
     * @param order zamówienie do zapisania; nie może być null
     * @return zapisane zamówienie z przypisanym ID (jeśli było nowe)
     * @throws IllegalArgumentException jeśli order jest null
     * @throws org.springframework.dao.DataAccessException w przypadku błędów bazy danych
     */
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    /**
     * Wyszukuje zamówienie na podstawie unikalnego identyfikatora.
     *
     * @param id identyfikator zamówienia do wyszukania; nie może być null
     * @return {@link Optional} zawierający zamówienie jeśli zostało znalezione, lub pusty Optional
     * @throws IllegalArgumentException jeśli id jest null
     */
    public Optional<Order> findOrderById(Long id) {
        return orderJpaRepository.findById(id);
    }

    /**
     * Wyszukuje wszystkie zamówienia o określonym priorytecie.
     *
     * @param orderPriority priorytet zamówień do wyszukania; nie może być null
     * @return lista zamówień o podanym priorytecie; może być pusta ale nigdy null
     * @throws IllegalArgumentException jeśli orderPriority jest null
     */
    public List<Order> findOrdersByOrderPriority(OrderPriority orderPriority) {
        return orderJpaRepository.findByOrderPriority(orderPriority);
    }

    /**
     * Wyszukuje wszystkie zamówienia o określonym statusie.
     *
     * @param orderStatus status zamówień do wyszukania; nie może być null
     * @return lista zamówień o podanym statusie; może być pusta ale nigdy null
     * @throws IllegalArgumentException jeśli orderStatus jest null
     */
    public List<Order> findOrdersByOrderStatus(OrderStatus orderStatus) {
        return orderJpaRepository.findByOrderStatus(orderStatus);
    }

    /**
     * Wyszukuje zamówienia o określonym statusie i priorytecie.
     *
     * <p>Metoda pozwala na precyzyjne filtrowanie zamówień według dwóch kryteriów jednocześnie.</p>
     *
     * @param orderStatus status zamówień do wyszukania; nie może być null
     * @param orderPriority priorytet zamówień do wyszukania; nie może być null
     * @return lista zamówień spełniających oba kryteria; może być pusta ale nigdy null
     * @throws IllegalArgumentException jeśli którykolwiek z parametrów jest null
     */
    public List<Order> findOrderByStatusAndOrderPriority(OrderStatus orderStatus, OrderPriority orderPriority) {
        return orderJpaRepository.findByOrderStatusAndOrderPriority(orderStatus, orderPriority);
    }

    /**
     * Wyszukuje wszystkie zamówienia od określonego dostawcy.
     *
     * @param supplier nazwa dostawcy zamówień do wyszukania; nie może być null ani pusty
     * @return lista zamówień od podanego dostawcy; może być pusta ale nigdy null
     * @throws IllegalArgumentException jeśli supplier jest null lub pusty
     */
    public List<Order> findOrdersBySupplier(String supplier) {
        return orderJpaRepository.findBySupplier(supplier);
    }

    /**
     * Wyszukuje wszystkie zamówienia złożone w określonym dniu.
     *
     * @param orderDate data złożenia zamówień do wyszukania; nie może być null
     * @return lista zamówień złożonych w podanej dacie; może być pusta ale nigdy null
     * @throws IllegalArgumentException jeśli orderDate jest null
     */
    public List<Order> findOrdersByOrderDate(LocalDate orderDate) {
        return orderJpaRepository.findByOrderDate(orderDate);
    }

    /**
     * Wyszukuje wszystkie zamówienia złożone w określonym momencie czasowym.
     *
     * <p>Metoda wyszukuje zamówienia na podstawie dokładnego znacznika czasu z precyzją do nanosekund.</p>
     *
     * @param orderTime dokładny czas złożenia zamówień do wyszukania; nie może być null
     * @return lista zamówień złożonych w podanym momencie; może być pusta ale nigdy null
     * @throws IllegalArgumentException jeśli orderTime jest null
     */
    public List<Order> findByOrderTime(Instant orderTime) {
        return orderJpaRepository.findByOrderTime(orderTime);
    }

    /**
     * Pobiera wszystkie zamówienia z obsługą stronicowania.
     *
     * <p>Metoda umożliwia efektywne pobieranie dużych zbiorów zamówień poprzez
     * podział wyników na strony o określonej wielkości.</p>
     *
     * @param pageable obiekt zawierający informacje o stronicowaniu (numer strony, rozmiar, sortowanie); nie może być null
     * @return strona zamówień zgodna z parametrami stronicowania
     * @throws IllegalArgumentException jeśli pageable jest null
     * @see org.springframework.data.domain.Pageable
     * @see org.springframework.data.domain.Page
     */
    public Page<Order> findAll(Pageable pageable) {
        return orderJpaRepository.findAll(pageable);
    }
}