package com.orange.bookmanagment.order.repository;

import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.OrderedBook;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testy jednostkowe dla OrderRepository")
class OrderRepositoryTests {

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @InjectMocks
    private OrderRepository orderRepository;

    private Order testOrder;
    private OrderedBook testOrderedBook;

    @BeforeEach
    void setUp() {
        // Przygotowanie testowych danych
        OrderedBook.OrderedBookAuthor author = new OrderedBook.OrderedBookAuthor(
                "Jan", "Kowalski", "Biografia autora"
        );
        OrderedBook.OrderedBookPublisher publisher = new OrderedBook.OrderedBookPublisher(
                "Wydawnictwo Test", "Opis wydawnictwa"
        );

        testOrderedBook = new OrderedBook(
                "Test Książka",
                List.of(author),
                publisher,
                "Opis książki",
                "Fantasy",
                "http://example.com/cover.jpg"
        );

        testOrder = new Order("Test Supplier", List.of(testOrderedBook), OrderPriority.HIGH);
        testOrder.setId(1L);
    }

    @Test
    @DisplayName("Powinien zapisać zamówienie")
    void shouldSaveOrder() {
        // Given
        when(orderJpaRepository.save(testOrder)).thenReturn(testOrder);

        // When
        Order result = orderRepository.save(testOrder);

        // Then
        assertThat(result).isEqualTo(testOrder);
        verify(orderJpaRepository).save(testOrder);
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienie po ID")
    void shouldFindOrderById() {
        // Given
        when(orderJpaRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Optional<Order> result = orderRepository.findOrderById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testOrder);
        verify(orderJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("Powinien zwrócić pusty Optional gdy zamówienie nie istnieje")
    void shouldReturnEmptyOptionalWhenOrderNotExists() {
        // Given
        when(orderJpaRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderRepository.findOrderById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(orderJpaRepository).findById(999L);
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po priorytecie")
    void shouldFindOrdersByPriority() {
        // Given
        List<Order> orders = List.of(testOrder);
        when(orderJpaRepository.findByOrderPriority(OrderPriority.HIGH)).thenReturn(orders);

        // When
        List<Order> result = orderRepository.findOrdersByOrderPriority(OrderPriority.HIGH);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderJpaRepository).findByOrderPriority(OrderPriority.HIGH);
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po statusie")
    void shouldFindOrdersByStatus() {
        // Given
        List<Order> orders = List.of(testOrder);
        when(orderJpaRepository.findByOrderStatus(OrderStatus.PLACED)).thenReturn(orders);

        // When
        List<Order> result = orderRepository.findOrdersByOrderStatus(OrderStatus.PLACED);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderJpaRepository).findByOrderStatus(OrderStatus.PLACED);
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po statusie i priorytecie")
    void shouldFindOrdersByStatusAndPriority() {
        // Given
        List<Order> orders = List.of(testOrder);
        when(orderJpaRepository.findByOrderStatusAndOrderPriority(OrderStatus.PLACED, OrderPriority.HIGH))
                .thenReturn(orders);

        // When
        List<Order> result = orderRepository.findOrderByStatusAndOrderPriority(OrderStatus.PLACED, OrderPriority.HIGH);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderJpaRepository).findByOrderStatusAndOrderPriority(OrderStatus.PLACED, OrderPriority.HIGH);
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po dostawcy")
    void shouldFindOrdersBySupplier() {
        // Given
        String supplier = "Test Supplier";
        List<Order> orders = List.of(testOrder);
        when(orderJpaRepository.findBySupplier(supplier)).thenReturn(orders);

        // When
        List<Order> result = orderRepository.findOrdersBySupplier(supplier);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderJpaRepository).findBySupplier(supplier);
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po dacie zamówienia")
    void shouldFindOrdersByOrderDate() {
        // Given
        LocalDate orderDate = LocalDate.of(2024, 1, 15);
        List<Order> orders = List.of(testOrder);
        when(orderJpaRepository.findByOrderDate(orderDate)).thenReturn(orders);

        // When
        List<Order> result = orderRepository.findOrdersByOrderDate(orderDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderJpaRepository).findByOrderDate(orderDate);
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po czasie zamówienia")
    void shouldFindOrdersByOrderTime() {
        // Given
        Instant orderTime = Instant.now();
        List<Order> orders = List.of(testOrder);
        when(orderJpaRepository.findByOrderTime(orderTime)).thenReturn(orders);

        // When
        List<Order> result = orderRepository.findByOrderTime(orderTime);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderJpaRepository).findByOrderTime(orderTime);
    }

    @Test
    @DisplayName("Powinien zwrócić stronicowane zamówienia")
    void shouldFindAllWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(testOrder), pageable, 1);
        when(orderJpaRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Order> result = orderRepository.findAll(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testOrder);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        verify(orderJpaRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Powinien zwrócić pustą listę gdy brak zamówień dla danego priorytetu")
    void shouldReturnEmptyListWhenNoOrdersForPriority() {
        // Given
        when(orderJpaRepository.findByOrderPriority(OrderPriority.LOW)).thenReturn(List.of());

        // When
        List<Order> result = orderRepository.findOrdersByOrderPriority(OrderPriority.LOW);

        // Then
        assertThat(result).isEmpty();
        verify(orderJpaRepository).findByOrderPriority(OrderPriority.LOW);
    }

    @Test
    @DisplayName("Powinien zwrócić pustą listę gdy brak zamówień dla danego statusu")
    void shouldReturnEmptyListWhenNoOrdersForStatus() {
        // Given
        when(orderJpaRepository.findByOrderStatus(OrderStatus.FINISHED)).thenReturn(List.of());

        // When
        List<Order> result = orderRepository.findOrdersByOrderStatus(OrderStatus.FINISHED);

        // Then
        assertThat(result).isEmpty();
        verify(orderJpaRepository).findByOrderStatus(OrderStatus.FINISHED);
    }

    @Test
    @DisplayName("Powinien zwrócić pustą listę gdy brak zamówień dla danego dostawcy")
    void shouldReturnEmptyListWhenNoOrdersForSupplier() {
        // Given
        String supplier = "Nieistniejący Dostawca";
        when(orderJpaRepository.findBySupplier(supplier)).thenReturn(List.of());

        // When
        List<Order> result = orderRepository.findOrdersBySupplier(supplier);

        // Then
        assertThat(result).isEmpty();
        verify(orderJpaRepository).findBySupplier(supplier);
    }

    @Test
    @DisplayName("Powinien zwrócić pustą stronę gdy brak zamówień")
    void shouldReturnEmptyPageWhenNoOrders() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(orderJpaRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<Order> result = orderRepository.findAll(pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(orderJpaRepository).findAll(pageable);
    }
}