package com.orange.bookmanagment.order.service;

import com.orange.bookmanagment.order.exception.InvalidOrderArgumentException;
import com.orange.bookmanagment.order.exception.OrderNotFoundException;
import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.OrderedBook;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import com.orange.bookmanagment.order.repository.OrderRepository;
import com.orange.bookmanagment.order.service.impl.OrderServiceImpl;
import com.orange.bookmanagment.order.web.requests.OrderCreateRequest;
import com.orange.bookmanagment.order.web.requests.OrderPriorityUpdateRequest;
import com.orange.bookmanagment.order.web.requests.OrderStatusUpdateRequest;
import com.orange.bookmanagment.shared.events.BookCreateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testy jednostkowe dla OrderServiceImpl")
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderedBook testOrderedBook;
    private OrderCreateRequest testCreateRequest;

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

        testCreateRequest = new OrderCreateRequest(
                "Test Supplier",
                List.of(testOrderedBook),
                "HIGH"
        );
    }

    @Test
    @DisplayName("Powinien utworzyć zamówienie z prawidłowymi danymi")
    void shouldCreateOrderWithValidData() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order result = orderService.createOrder(testCreateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSupplier()).isEqualTo("Test Supplier");
        assertThat(result.getOrderPriority()).isEqualTo(OrderPriority.HIGH);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy nieprawidłowym priorytecie podczas tworzenia")
    void shouldThrowExceptionWhenCreatingOrderWithInvalidPriority() {
        // Given
        OrderCreateRequest invalidRequest = new OrderCreateRequest(
                "Test Supplier",
                List.of(testOrderedBook),
                "INVALID_PRIORITY"
        );

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(invalidRequest))
                .isInstanceOf(InvalidOrderArgumentException.class)
                .hasMessage("Invalid order priority");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienie po ID")
    void shouldFindOrderById() {
        // Given
        when(orderRepository.findOrderById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.getOrderById(1L);

        // Then
        assertThat(result).isEqualTo(testOrder);
        verify(orderRepository).findOrderById(1L);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek gdy zamówienie nie zostanie znalezione")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findOrderById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order by id not found");
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po priorytecie")
    void shouldFindOrdersByPriority() {
        // Given
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findOrdersByOrderPriority(OrderPriority.HIGH)).thenReturn(orders);

        // When
        List<Order> result = orderService.getOrdersByOrderPriority("HIGH");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderRepository).findOrdersByOrderPriority(OrderPriority.HIGH);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy nieprawidłowym priorytecie")
    void shouldThrowExceptionForInvalidPriority() {
        // When & Then
        assertThatThrownBy(() -> orderService.getOrdersByOrderPriority("INVALID"))
                .isInstanceOf(InvalidOrderArgumentException.class)
                .hasMessage("Invalid order priority");
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po statusie")
    void shouldFindOrdersByStatus() {
        // Given
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findOrdersByOrderStatus(OrderStatus.PLACED)).thenReturn(orders);

        // When
        List<Order> result = orderService.getOrdersByOrderStatus("PLACED");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderRepository).findOrdersByOrderStatus(OrderStatus.PLACED);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy nieprawidłowym statusie")
    void shouldThrowExceptionForInvalidStatus() {
        // When & Then
        assertThatThrownBy(() -> orderService.getOrdersByOrderStatus("INVALID"))
                .isInstanceOf(InvalidOrderArgumentException.class)
                .hasMessage("Invalid order status");
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po priorytecie i statusie")
    void shouldFindOrdersByPriorityAndStatus() {
        // Given
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findOrderByStatusAndOrderPriority(OrderStatus.PLACED, OrderPriority.HIGH))
                .thenReturn(orders);

        // When
        List<Order> result = orderService.getOrdersByOrderPriorityAndStatus("HIGH", "PLACED");

        // Then
        assertThat(result).hasSize(1);
        verify(orderRepository).findOrderByStatusAndOrderPriority(OrderStatus.PLACED, OrderPriority.HIGH);
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po dostawcy")
    void shouldFindOrdersBySupplier() {
        // Given
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findOrdersBySupplier("Test Supplier")).thenReturn(orders);

        // When
        List<Order> result = orderService.findOrdersBySupplier("Test Supplier");

        // Then
        assertThat(result).hasSize(1);
        verify(orderRepository).findOrdersBySupplier("Test Supplier");
    }

    @Test
    @DisplayName("Powinien znaleźć zamówienia po dacie")
    void shouldFindOrdersByDate() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findOrdersByOrderDate(testDate)).thenReturn(orders);

        // When
        List<Order> result = orderService.findOrdersByOrderDate("2024-01-15");

        // Then
        assertThat(result).hasSize(1);
        verify(orderRepository).findOrdersByOrderDate(testDate);
    }

    @Test
    @DisplayName("Powinien zwrócić stronicowane zamówienia")
    void shouldFindOrdersWithPagination() {
        // Given
        Page<Order> page = new PageImpl<>(List.of(testOrder));
        when(orderRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        // When
        Page<Order> result = orderService.findOrders(0, 10);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testOrder);
        verify(orderRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie dostępne priorytety")
    void shouldGetOrderPriorities() {
        // When
        Set<String> result = orderService.getOrderPriorities();

        // Then
        assertThat(result).contains("LOW", "MEDIUM", "HIGH");
        assertThat(result).hasSize(OrderPriority.values().length);
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie dostępne statusy")
    void shouldGetOrderStatuses() {
        // When
        Set<String> result = orderService.getOrderStatuses();

        // Then
        assertThat(result).contains("PLACED", "PROCESSING", "FINISHED");
        assertThat(result).hasSize(OrderStatus.values().length);
    }


    @Test
    @DisplayName("Powinien zaktualizować priorytet zamówienia")
    void shouldUpdateOrderPriority() {
        // Given
        OrderPriorityUpdateRequest request = new OrderPriorityUpdateRequest(1L, "LOW");
        when(orderRepository.findOrderById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        // When
        Order result = orderService.updateOrderPriority(request);

        // Then
        assertThat(result.getOrderPriority()).isEqualTo(OrderPriority.LOW);
        verify(orderRepository).findOrderById(1L);
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("Powinien sfinalizować zamówienie i opublikować eventy")
    void shouldFinishOrderAndPublishEvents() {
        // Given
        when(orderRepository.findOrderById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        // When
        Order result = orderService.finishOrder(1L);

        // Then
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.FINISHED);

        // Sprawdzenie czy event został opublikowany
        ArgumentCaptor<BookCreateEvent> eventCaptor = ArgumentCaptor.forClass(BookCreateEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        BookCreateEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.title()).isEqualTo("Test Książka");
        assertThat(publishedEvent.genre()).isEqualTo("Fantasy");

        verify(orderRepository).findOrderById(1L);
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy finalizacji nieistniejącego zamówienia")
    void shouldThrowExceptionWhenFinishingNonExistentOrder() {
        // Given
        when(orderRepository.findOrderById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.finishOrder(999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order by id not found");

        verify(eventPublisher, never()).publishEvent(any());
        verify(orderRepository, never()).save(any());
    }
}