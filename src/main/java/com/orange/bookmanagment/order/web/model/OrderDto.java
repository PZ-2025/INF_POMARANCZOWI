package com.orange.bookmanagment.order.web.model;

import com.orange.bookmanagment.order.model.OrderedBook;
import com.orange.bookmanagment.order.model.enums.OrderPriority;
import com.orange.bookmanagment.order.model.enums.OrderStatus;

import java.util.List;

public record OrderDto(
        long id,
        String supplier,
        List<OrderedBook> orderedBooks,
        OrderPriority orderPriority,
        OrderStatus orderStatus,
        String orderTime
) {
}
