package com.orange.bookmanagment.order.repository;

import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
interface OrderJpaRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderPriority(OrderPriority orderPriority);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    List<Order> findByOrderStatusAndOrderPriority(OrderStatus orderStatus, OrderPriority orderPriority);

    List<Order> findBySupplier(String supplier);

    List<Order> findByOrderDate(LocalDate date);

    Page<Order> findAll(Pageable pageable);
}
