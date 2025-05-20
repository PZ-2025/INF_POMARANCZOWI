package com.orange.bookmanagment.order.web.controller;

import com.orange.bookmanagment.order.service.OrderService;
import com.orange.bookmanagment.order.web.mapper.OrderDtoMapper;
import com.orange.bookmanagment.order.web.model.OrderDto;
import com.orange.bookmanagment.order.web.requests.OrderCreateRequest;
import com.orange.bookmanagment.order.web.requests.OrderPriorityUpdateRequest;
import com.orange.bookmanagment.order.web.requests.OrderStatusUpdateRequest;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/order")

@RequiredArgsConstructor
class OrderController {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;

    @PostMapping("/place")
    public ResponseEntity<HttpResponse> placeOrder(@Valid @RequestBody OrderCreateRequest orderCreateRequest) {

        final OrderDto order = orderDtoMapper.toDto(orderService.createOrder(orderCreateRequest));

        return ResponseEntity.status(CREATED).body(HttpResponse.builder()
                        .httpStatus(CREATED)
                        .statusCode(CREATED.value())
                        .reason("Place order request")
                        .message("Order has been placed")
                        .data(Map.of("order", order))
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> findOrderById(@PathVariable("id") Long id) {

        final OrderDto orderDto = orderDtoMapper.toDto(orderService.getOrderById(id));

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Order has been found")
                .message("Order by id")
                .data(Map.of("order", orderDto))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    @GetMapping("/all")
    public ResponseEntity<HttpResponse> findAllOrders(
            @RequestParam @Valid @Min(value = 0, message = "Numer strony musi być wiekszy niż 0") int page,
            @RequestParam @Valid @Min(value = 0, message = "Rozmiar strony musi być większy niż 0") int size) {

        final List<OrderDto> orders = orderService.findOrders(page,size).stream().map(orderDtoMapper::toDto).toList();

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Orders request")
                .message("Orders found")
                .data(Map.of("orders", orders))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<HttpResponse> findOrdersByPriority(@PathVariable @Valid @NotNull String priority) {

        final List<OrderDto> orders = orderService.getOrdersByOrderPriority(priority).stream().map(orderDtoMapper::toDto).toList();

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Orders request")
                .message("Orders found")
                .data(Map.of("orders", orders))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<HttpResponse> findOrdersByStatus(@PathVariable @Valid @NotNull String status) {

        final List<OrderDto> orders = orderService.getOrdersByOrderStatus(status).stream().map(orderDtoMapper::toDto).toList();

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Orders request")
                .message("Orders found")
                .data(Map.of("orders", orders))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    @PostMapping("/finish/{id}")
    public ResponseEntity<HttpResponse> finishOrder(
            @PathVariable @Valid @NotNull @Min(value = 0, message = "Id zamówienia musi być wieksze niż 0") int id) {

        final OrderDto orderDto = orderDtoMapper.toDto(orderService.finishOrder(id));

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .httpStatus(OK)
                        .statusCode(OK.value())
                        .data(Map.of("order", orderDto))
                        .message("Order has been finished")
                        .reason("Order finish request")
                .build());
    }

    @PatchMapping("/priority")
    public ResponseEntity<HttpResponse> updateOrderPriority(
            @Valid @RequestBody OrderPriorityUpdateRequest orderPriorityUpdateRequest) {

        final OrderDto orderDto = orderDtoMapper.toDto(orderService.updateOrderPriority(orderPriorityUpdateRequest));

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Order priority update")
                .message("Order has been updated")
                .data(Map.of("order", orderDto))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    @PatchMapping("/status")
    public ResponseEntity<HttpResponse> updateOrderStatus(
            @Valid @RequestBody OrderStatusUpdateRequest orderStatusUpdateRequest) {

        final OrderDto orderDto = orderDtoMapper.toDto(orderService.updateOrderStatus(orderStatusUpdateRequest));

        return ResponseEntity.status(OK).body(HttpResponse.builder()
                .httpStatus(OK)
                .statusCode(OK.value())
                .reason("Order status update")
                .message("Order has been updated")
                .data(Map.of("order", orderDto))
                .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                .build());
    }

    @GetMapping("/data/{type}")
    public ResponseEntity<HttpResponse> getOrderData(@PathVariable @Valid @NotNull String type) {
        return switch (type) {
            case "priorities" -> ResponseEntity.status(OK).body(HttpResponse.builder()
                    .httpStatus(OK)
                    .statusCode(OK.value())
                    .reason("Order type data request")
                    .message("Order priorities")
                    .data(Map.of("priorities", orderService.getOrderPriorities()))
                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                    .build());
            case "status" -> ResponseEntity.status(OK).body(HttpResponse.builder()
                    .httpStatus(OK)
                    .statusCode(OK.value())
                    .reason("Order type data request")
                    .message("Order statues")
                    .data(Map.of("priorities", orderService.getOrderStatuses()))
                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                    .build());
            default -> ResponseEntity.status(BAD_REQUEST).body(HttpResponse.builder()
                    .httpStatus(BAD_REQUEST)
                    .statusCode(BAD_REQUEST.value())
                    .reason("Order type data request")
                    .message("Order data not found")
                    .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                    .build());
        };
    }
}
