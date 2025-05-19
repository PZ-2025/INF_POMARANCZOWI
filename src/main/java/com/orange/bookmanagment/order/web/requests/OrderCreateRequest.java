package com.orange.bookmanagment.order.web.requests;

import com.orange.bookmanagment.order.model.OrderedBook;

import java.util.List;

public record OrderCreateRequest(

        String supplier,
        List<OrderedBook> orderedBooks,
        String orderPriority
) {


}
