package com.orange.bookmanagment.order.repository;

import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    public Optional<Order> findOrderById(Long id) {
        return orderJpaRepository.findById(id);
    }

    public List<Order> findOrdersByOrderPriority(OrderPriority orderPriority){
        return orderJpaRepository.findByOrderPriority(orderPriority);
    }

    public List<Order> findOrdersByOrderStatus(OrderStatus orderStatus){
        return orderJpaRepository.findByOrderStatus(orderStatus);
    }

    public List<Order> findOrderByStatusAndOrderPriority(OrderStatus orderStatus, OrderPriority orderPriority){
        return orderJpaRepository.findByOrderStatusAndOrderPriority(orderStatus, orderPriority);
    }

    public List<Order> findOrdersBySupplier(String supplier){
        return orderJpaRepository.findBySupplier(supplier);
    }

    public List<Order> findOrdersByOrderDate(LocalDate orderDate){
        return orderJpaRepository.findByOrderDate(orderDate);
    }

    public Page<Order> findAll(Pageable pageable){
        return orderJpaRepository.findAll(pageable);
    }


}
