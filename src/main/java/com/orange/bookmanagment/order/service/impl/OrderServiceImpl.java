package com.orange.bookmanagment.order.service.impl;

import com.orange.bookmanagment.order.exception.InvalidOrderArgumentException;
import com.orange.bookmanagment.order.exception.OrderNotFoundException;
import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.OrderedBook;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import com.orange.bookmanagment.order.repository.OrderRepository;
import com.orange.bookmanagment.order.service.OrderService;
import com.orange.bookmanagment.order.web.requests.OrderCreateRequest;
import com.orange.bookmanagment.order.web.requests.OrderPriorityUpdateRequest;
import com.orange.bookmanagment.order.web.requests.OrderStatusUpdateRequest;
import com.orange.bookmanagment.shared.events.BookCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Order createOrder(OrderCreateRequest orderCreateRequest) throws InvalidOrderArgumentException {

        if (!OrderPriority.existByName(orderCreateRequest.orderPriority())) {
            throw new InvalidOrderArgumentException("Invalid order priority");
        }

        final OrderPriority orderPriority = OrderPriority.valueOf(orderCreateRequest.orderPriority());
        final Order order = new Order(orderCreateRequest.supplier(),orderCreateRequest.orderedBooks(), orderPriority);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(long id) throws OrderNotFoundException {
        return orderRepository.findOrderById(id).orElseThrow(() -> new OrderNotFoundException("Order by id not found"));
    }

    @Override
    public List<Order> getOrdersByOrderPriority(String orderPriority) throws InvalidOrderArgumentException {
        if (!OrderPriority.existByName(orderPriority)) {
            throw new InvalidOrderArgumentException("Invalid order priority");
        }

        return orderRepository.findOrdersByOrderPriority(OrderPriority.valueOf(orderPriority));
    }

    @Override
    public List<Order> getOrdersByOrderStatus(String orderStatus) throws InvalidOrderArgumentException {
        if (!OrderStatus.existByName(orderStatus)) {
            throw new InvalidOrderArgumentException("Invalid order status");
        }

        return orderRepository.findOrdersByOrderStatus(OrderStatus.valueOf(orderStatus));
    }

    @Override
    public List<Order> getOrdersByOrderPriorityAndStatus(String orderPriority, String orderStatus) throws InvalidOrderArgumentException {
        if (!OrderPriority.existByName(orderPriority)) {
            throw new InvalidOrderArgumentException("Invalid order priority");
        }

        if (!OrderStatus.existByName(orderStatus)) {
            throw new InvalidOrderArgumentException("Invalid order status");
        }

        final OrderStatus status = OrderStatus.valueOf(orderStatus);
        final  OrderPriority priority = OrderPriority.valueOf(orderPriority);

        return orderRepository.findOrderByStatusAndOrderPriority(status,priority);
    }

    @Override
    public List<Order> findOrdersBySupplier(String supplier) {
        return orderRepository.findOrdersBySupplier(supplier);
    }

    @Override
    public List<Order> findOrdersByOrderDate(String orderDate) {
        final LocalDate date = LocalDate.parse(orderDate);

        return orderRepository.findOrdersByOrderDate(date);
    }

    @Override
    public Page<Order> findOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Set<String> getOrderPriorities() {
        return Arrays.stream(OrderPriority.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getOrderStatuses() {
        return Arrays.stream(OrderStatus.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public Order updateOrderStatus(OrderStatusUpdateRequest orderStatusUpdateRequest) throws InvalidOrderArgumentException, OrderNotFoundException {
        final Order order = orderRepository.findOrderById(orderStatusUpdateRequest.orderId()).orElseThrow(() -> new OrderNotFoundException("Order by id not found"));

        if (!OrderStatus.existByName(orderStatusUpdateRequest.orderStatus())) {
            throw new InvalidOrderArgumentException("Invalid order status");
        }

        final OrderStatus status = OrderStatus.valueOf(orderStatusUpdateRequest.orderStatus());

        order.updateOrderStatus(status);

        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderPriority(OrderPriorityUpdateRequest orderPriorityUpdateRequest) throws InvalidOrderArgumentException, OrderNotFoundException {
        final Order order = orderRepository.findOrderById(orderPriorityUpdateRequest.orderId()).orElseThrow(() -> new OrderNotFoundException("Order by id not found"));

        if (!OrderPriority.existByName(orderPriorityUpdateRequest.orderPriority())) {
            throw new InvalidOrderArgumentException("Invalid order priority");
        }

        final OrderPriority priority = OrderPriority.valueOf(orderPriorityUpdateRequest.orderPriority());

        order.updateOrderPriority(priority);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order finishOrder(long id) throws OrderNotFoundException {
        final Order order = orderRepository.findOrderById(id).orElseThrow(() -> new OrderNotFoundException("Order by id not found"));

        for (OrderedBook orderedBook : order.getOrderedBooks()) {
            eventPublisher.publishEvent(
                    new BookCreateEvent(orderedBook.title(),orderedBook.authors().stream().map(orderedBookAuthor -> new BookCreateEvent.EventBookAuthor(orderedBookAuthor.firstName(),orderedBookAuthor.lastName(),orderedBookAuthor.biography())).toList(),
                    new BookCreateEvent.EventBookPublisher(orderedBook.publisher().name(),orderedBook.publisher().description()),orderedBook.description(),orderedBook.genre(),orderedBook.coverImage()));
        }

        order.updateOrderStatus(OrderStatus.FINISHED);

        return orderRepository.save(order);
    }
}
