package com.orange.bookmanagment.order.web.requests;

public record OrderStatusUpdateRequest(
        long orderId,
        String orderStatus
) { }
