package com.orange.bookmanagment.order.model;


import com.orange.bookmanagment.order.converter.OrderedBookConverter;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Encja JPA reprezentująca zamówienie książek w systemie zarządzania księgarnią.
 *
 * <p>Klasa Order przechowuje wszystkie informacje związane z zamówieniem, włączając w to
 * dostawcę, listę zamówionych książek, priorytet, status oraz czasy związane z zamówieniem.</p>
 *
 * <p>Lista zamówionych książek jest konwertowana do formatu JSON przy zapisie do bazy danych
 * za pomocą {@link OrderedBookConverter}. Status zamówienia jest automatycznie ustawiany na
 * {@link OrderStatus#PLACED} przy tworzeniu nowego zamówienia.</p>
 *
 * <p>Klasa automatycznie zarządza czasami zamówienia - ustawia czas utworzenia przy inicjalizacji
 * oraz aktualizuje czas modyfikacji przy zmianach statusu lub priorytetu.</p>
 *
 * @since 1.0
 * @see OrderedBook
 * @see OrderPriority
 * @see OrderStatus
 * @see OrderedBookConverter
 */
@Entity(name = "order")
@Table(name = "orders")
@NoArgsConstructor
@Getter
@Setter
public class Order {

    /**
     * Unikalny identyfikator zamówienia generowany automatycznie przez bazę danych.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * Nazwa dostawcy od którego składane jest zamówienie.
     *
     * <p>Pole przechowuje nazwę firmy lub osoby, która będzie realizować zamówienie książek.</p>
     */
    private String supplier;

    /**
     * Lista książek zamówionych w ramach tego zamówienia.
     *
     * <p>Lista jest konwertowana do formatu JSON przy zapisie do bazy danych za pomocą
     * {@link OrderedBookConverter}. Domyślnie inicjalizowana jako pusta {@link ArrayList}.</p>
     */
    @Convert(converter = OrderedBookConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<OrderedBook> orderedBooks = new ArrayList<>();

    /**
     * Priorytet zamówienia określający jego ważność w kolejce realizacji.
     *
     * <p>Wartość przechowywana jako string w bazie danych za pomocą {@link EnumType#STRING}.</p>
     */
    @Enumerated(EnumType.STRING)
    private OrderPriority orderPriority;

    /**
     * Aktualny status zamówienia w procesie realizacji.
     *
     * <p>Wartość przechowywana jako string w bazie danych za pomocą {@link EnumType#STRING}.
     * Domyślnie ustawiana na {@link OrderStatus#PLACED} przy tworzeniu nowego zamówienia.</p>
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    /**
     * Znacznik czasu utworzenia zamówienia z precyzją do nanosekund.
     *
     * <p>Automatycznie ustawiany na aktualny czas przy tworzeniu zamówienia.</p>
     */
    private Instant orderTime;

    /**
     * Znacznik czasu ostatniej modyfikacji zamówienia z precyzją do nanosekund.
     *
     * <p>Aktualizowany automatycznie przy każdej zmianie statusu lub priorytetu zamówienia.</p>
     */
    private Instant updateTime;

    /**
     * Data złożenia zamówienia bez informacji o czasie.
     *
     * <p>Automatycznie ustawiana na aktualną datę przy tworzeniu zamówienia.</p>
     */
    private LocalDate orderDate;

    /**
     * Konstruktor tworzący nowe zamówienie z podstawowymi parametrami.
     *
     * <p>Automatycznie ustawia status zamówienia na {@link OrderStatus#PLACED},
     * czas zamówienia na aktualny {@link Instant} oraz datę zamówienia na aktualną {@link LocalDate}.</p>
     *
     * @param supplier nazwa dostawcy realizującego zamówienie; nie może być null
     * @param orderedBooks lista książek do zamówienia; nie może być null
     * @param orderPriority priorytet zamówienia; nie może być null
     * @throws NullPointerException jeśli którykolwiek z parametrów jest null
     */
    public Order(String supplier, List<OrderedBook> orderedBooks, OrderPriority orderPriority) {
        this.supplier = supplier;
        this.orderedBooks = orderedBooks;
        this.orderPriority = orderPriority;
        this.orderStatus = OrderStatus.PLACED;
        this.orderTime = Instant.now();
        this.orderDate = LocalDate.now();
    }

    /**
     * Aktualizuje status zamówienia i ustawia czas ostatniej modyfikacji.
     *
     * <p>Metoda zmienia status zamówienia na podany oraz automatycznie aktualizuje
     * pole {@link #updateTime} na aktualny moment.</p>
     *
     * @param orderStatus nowy status zamówienia; nie może być null
     * @throws NullPointerException jeśli orderStatus jest null
     */
    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.updateTime = Instant.now();
    }

    /**
     * Aktualizuje priorytet zamówienia i ustawia czas ostatniej modyfikacji.
     *
     * <p>Metoda zmienia priorytet zamówienia na podany oraz automatycznie aktualizuje
     * pole {@link #updateTime} na aktualny moment.</p>
     *
     * @param orderPriority nowy priorytet zamówienia; nie może być null
     * @throws NullPointerException jeśli orderPriority jest null
     */
    public void updateOrderPriority(OrderPriority orderPriority) {
        this.orderPriority = orderPriority;
        this.updateTime = Instant.now();
    }
}