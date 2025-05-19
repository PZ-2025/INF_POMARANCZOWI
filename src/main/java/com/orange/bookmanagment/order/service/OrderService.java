package com.orange.bookmanagment.order.service;

import com.orange.bookmanagment.order.exception.InvalidOrderArgumentException;
import com.orange.bookmanagment.order.exception.OrderNotFoundException;
import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import com.orange.bookmanagment.order.web.requests.OrderCreateRequest;
import com.orange.bookmanagment.order.web.requests.OrderPriorityUpdateRequest;
import com.orange.bookmanagment.order.web.requests.OrderStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface OrderService {

   Order createOrder(OrderCreateRequest orderCreateRequest) throws InvalidOrderArgumentException;

   Order getOrderById(long id) throws OrderNotFoundException;

   List<Order> getOrdersByOrderPriority(String orderPriority) throws InvalidOrderArgumentException;

   List<Order> getOrdersByOrderStatus(String orderStatus) throws InvalidOrderArgumentException;

   List<Order> getOrdersByOrderPriorityAndStatus(String orderPriority, String orderStatus) throws InvalidOrderArgumentException;

   List<Order> findOrdersBySupplier(String supplier);

   List<Order> findOrdersByOrderDate(String orderDate);

   Page<Order> findOrders(int page, int size);

   Set<String> getOrderPriorities();

   Set<String> getOrderStatuses();

   Order updateOrderStatus(OrderStatusUpdateRequest orderStatusUpdateRequest) throws InvalidOrderArgumentException, OrderNotFoundException;

   Order updateOrderPriority(OrderPriorityUpdateRequest orderPriorityUpdateRequest) throws InvalidOrderArgumentException, OrderNotFoundException;


}
