package com.orange.bookmanagment.order.web.requests;

public record OrderPriorityUpdateRequest(
        long orderId,
        String orderPriority
) {
}
