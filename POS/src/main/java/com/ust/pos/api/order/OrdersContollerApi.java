package com.ust.pos.api.order;

import com.ust.pos.dto.OrdersDto;
import com.ust.pos.orders.service.OrdersService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersContollerApi {

    private final OrdersService ordersService;

    public OrdersContollerApi(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping("/generateId")
    public String generateOrderId(@RequestBody OrdersDto ordersDto) {
        return ordersService.generateOrderId(ordersDto.getIdentifier());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('Admin','Employee')")
    public OrdersDto createOrder(@RequestBody OrdersDto orderDto) {
        return ordersService.placeOrder(orderDto.getIdentifier(), orderDto.getPaymentMode());
    }

    @GetMapping("list")
    @PreAuthorize("hasAnyAuthority('Admin','Employee')")
    public List<OrdersDto> getAllOrders() {
        return ordersService.findAll();
    }
}