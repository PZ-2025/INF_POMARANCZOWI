package com.orange.bookmanagment.order.web.requests;

import com.orange.bookmanagment.order.model.OrderedBook;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.List;

public record OrderCreateRequest(
        @NotNull(message = "Dostawca nie może być pusty!")
        String supplier,
        List<OrderedBook> orderedBooks,
        @NotNull(message = "Priorytet musi być określony!")
        String orderPriority
) { }
