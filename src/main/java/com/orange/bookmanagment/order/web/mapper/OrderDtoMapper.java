package com.orange.bookmanagment.order.web.mapper;


import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.web.model.OrderDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.stereotype.Component;

@Component
public class OrderDtoMapper {

    public OrderDto toDto(Order order) {
        return new OrderDto(order.getId(),order.getSupplier(),order.getOrderedBooks(),order.getOrderPriority(),order.getOrderStatus(), TimeUtil.getTimeInStandardFormat(order.getOrderTime()));
    }
}
