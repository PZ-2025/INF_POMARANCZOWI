package com.orange.bookmanagment.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.bookmanagment.order.exception.InvalidOrderArgumentException;
import com.orange.bookmanagment.order.exception.OrderNotFoundException;
import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.OrderedBook;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import com.orange.bookmanagment.order.service.OrderService;
import com.orange.bookmanagment.order.web.controller.OrderController;
import com.orange.bookmanagment.order.web.mapper.OrderDtoMapper;
import com.orange.bookmanagment.order.web.model.OrderDto;
import com.orange.bookmanagment.order.web.requests.OrderCreateRequest;
import com.orange.bookmanagment.order.web.requests.OrderPriorityUpdateRequest;
import com.orange.bookmanagment.order.web.requests.OrderStatusUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("Testy jednostkowe dla OrderController")
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderDtoMapper orderDtoMapper;

    private Order testOrder;
    private OrderDto testOrderDto;
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

        testOrderDto = new OrderDto(
                1L,
                "Test Supplier",
                List.of(testOrderedBook),
                OrderPriority.HIGH,
                OrderStatus.PLACED,
                "2024-01-15 10:30:00"
        );
    }

    @Test
    @DisplayName("POST /api/v1/order/place - Powinien utworzyć zamówienie")
    void shouldCreateOrder() throws Exception {
        // Given
        OrderCreateRequest request = new OrderCreateRequest(
                "Test Supplier",
                List.of(testOrderedBook),
                "HIGH"
        );

        when(orderService.createOrder(any(OrderCreateRequest.class))).thenReturn(testOrder);
        when(orderDtoMapper.toDto(testOrder)).thenReturn(testOrderDto);

        // When & Then
        mockMvc.perform(post("/api/v1/order/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Order has been placed"))
                .andExpect(jsonPath("$.data.order.id").value(1))
                .andExpect(jsonPath("$.data.order.supplier").value("Test Supplier"));
    }

    @Test
    @DisplayName("POST /api/v1/order/place - Powinien zwrócić błąd dla nieprawidłowych danych")
    void shouldReturnBadRequestForInvalidOrderData() throws Exception {
        // Given
        OrderCreateRequest request = new OrderCreateRequest(
                "Test Supplier",
                List.of(testOrderedBook),
                "INVALID_PRIORITY"
        );

        when(orderService.createOrder(any(OrderCreateRequest.class)))
                .thenThrow(new InvalidOrderArgumentException("Invalid order priority"));

        // When & Then
        mockMvc.perform(post("/api/v1/order/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/order/{id} - Powinien zwrócić zamówienie po ID")
    void shouldReturnOrderById() throws Exception {
        // Given
        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        when(orderDtoMapper.toDto(testOrder)).thenReturn(testOrderDto);

        // When & Then
        mockMvc.perform(get("/api/v1/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Order by id"))
                .andExpect(jsonPath("$.data.order.id").value(1))
                .andExpect(jsonPath("$.data.order.supplier").value("Test Supplier"));
    }

    @Test
    @DisplayName("GET /api/v1/order/{id} - Powinien zwrócić 404 dla nieistniejącego zamówienia")
    void shouldReturn404ForNonExistentOrder() throws Exception {
        // Given
        when(orderService.getOrderById(999L))
                .thenThrow(new OrderNotFoundException("Order by id not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/order/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/order/all - Powinien zwrócić stronicowaną listę zamówień")
    void shouldReturnPaginatedOrders() throws Exception {
        // Given
        when(orderService.findOrders(0, 10)).thenReturn(new PageImpl<>(List.of(testOrder)));
        when(orderDtoMapper.toDto(testOrder)).thenReturn(testOrderDto);

        // When & Then
        mockMvc.perform(get("/api/v1/order/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Orders found"))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/order/all - Powinien zwrócić błąd dla nieprawidłowych parametrów stronicowania")
    void shouldReturnBadRequestForInvalidPaginationParams() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/order/all")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/order/priority/{priority} - Powinien zwrócić zamówienia po priorytecie")
    void shouldReturnOrdersByPriority() throws Exception {
        // Given
        when(orderService.getOrdersByOrderPriority("HIGH")).thenReturn(List.of(testOrder));
        when(orderDtoMapper.toDto(testOrder)).thenReturn(testOrderDto);

        // When & Then
        mockMvc.perform(get("/api/v1/order/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Orders found"))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders[0].orderPriority").value("HIGH"));
    }

    @Test
    @DisplayName("GET /api/v1/order/status/{status} - Powinien zwrócić zamówienia po statusie")
    void shouldReturnOrdersByStatus() throws Exception {
        // Given
        when(orderService.getOrdersByOrderStatus("PLACED")).thenReturn(List.of(testOrder));
        when(orderDtoMapper.toDto(testOrder)).thenReturn(testOrderDto);

        // When & Then
        mockMvc.perform(get("/api/v1/order/status/PLACED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Orders found"))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders[0].orderStatus").value("PLACED"));
    }

    @Test
    @DisplayName("POST /api/v1/order/finish/{id} - Powinien sfinalizować zamówienie")
    void shouldFinishOrder() throws Exception {
        // Given
        Order finishedOrder = testOrder;
        finishedOrder.updateOrderStatus(OrderStatus.FINISHED);

        OrderDto finishedOrderDto = new OrderDto(
                1L, "Test Supplier", List.of(testOrderedBook),
                OrderPriority.HIGH, OrderStatus.FINISHED, "2024-01-15 10:30:00"
        );

        when(orderService.finishOrder(1L)).thenReturn(finishedOrder);
        when(orderDtoMapper.toDto(finishedOrder)).thenReturn(finishedOrderDto);

        // When & Then
        mockMvc.perform(post("/api/v1/order/finish/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Order has been finished"))
                .andExpect(jsonPath("$.data.order.orderStatus").value("FINISHED"));
    }

    @Test
    @DisplayName("PATCH /api/v1/order/priority - Powinien zaktualizować priorytet zamówienia")
    void shouldUpdateOrderPriority() throws Exception {
        // Given
        OrderPriorityUpdateRequest request = new OrderPriorityUpdateRequest(1L, "LOW");
        Order updatedOrder = testOrder;
        updatedOrder.updateOrderPriority(OrderPriority.LOW);

        OrderDto updatedOrderDto = new OrderDto(
                1L, "Test Supplier", List.of(testOrderedBook),
                OrderPriority.LOW, OrderStatus.PLACED, "2024-01-15 10:30:00"
        );

        when(orderService.updateOrderPriority(any(OrderPriorityUpdateRequest.class)))
                .thenReturn(updatedOrder);
        when(orderDtoMapper.toDto(updatedOrder)).thenReturn(updatedOrderDto);

        // When & Then
        mockMvc.perform(patch("/api/v1/order/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Order has been updated"))
                .andExpect(jsonPath("$.data.order.orderPriority").value("LOW"));
    }

    @Test
    @DisplayName("GET /api/v1/order/data/priorities - Powinien zwrócić dostępne priorytety")
    void shouldReturnAvailablePriorities() throws Exception {
        // Given
        when(orderService.getOrderPriorities()).thenReturn(Set.of("LOW", "MEDIUM", "HIGH"));

        // When & Then
        mockMvc.perform(get("/api/v1/order/data/priorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Order priorities"))
                .andExpect(jsonPath("$.data.priorities").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/order/data/status - Powinien zwrócić dostępne statusy")
    void shouldReturnAvailableStatuses() throws Exception {
        // Given
        when(orderService.getOrderStatuses()).thenReturn(Set.of("PLACED", "PROCESSING", "FINISHED"));

        // When & Then
        mockMvc.perform(get("/api/v1/order/data/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Order statues"))
                .andExpect(jsonPath("$.data.priorities").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/order/data/{type} - Powinien zwrócić błąd dla nieprawidłowego typu")
    void shouldReturnBadRequestForInvalidDataType() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/order/data/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Order data not found"));
    }
}