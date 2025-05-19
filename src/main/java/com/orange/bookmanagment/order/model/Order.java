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

@Entity(name = "order")
@Table(name = "orders")
@NoArgsConstructor
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String supplier;

    @Convert(converter = OrderedBookConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<OrderedBook> orderedBooks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderPriority orderPriority;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDate orderDate;

    private Instant orderTime;

    private LocalDateTime updateTime;


    public Order(String supplier, List<OrderedBook> orderedBooks, OrderPriority orderPriority) {
        this.supplier = supplier;
        this.orderedBooks = orderedBooks;
        this.orderPriority = orderPriority;
        this.orderStatus = OrderStatus.PLACED;
        this.orderDate = LocalDate.now();
        this.orderTime = Instant.now();
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.updateTime = LocalDateTime.now();
    }

    public void updateOrderPriority(OrderPriority orderPriority) {
        this.orderPriority = orderPriority;
        this.updateTime = LocalDateTime.now();
    }
}
