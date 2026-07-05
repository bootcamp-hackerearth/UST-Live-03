package com.ust.pos.api.order;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderApiController {
    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/generateId")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public String generateOrderId(@RequestBody OrderDto orderDto) {
        return orderService.generateOrderId(orderDto.getIdentifier());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public OrderDto createOrder(@RequestBody OrderDto orderDto) {
        return orderService.placeOrder(orderDto.getIdentifier(), orderDto.getPaymentMode());
    }

    @GetMapping("list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','SUPPORT','ACCOUNTANT')")
    public List<OrderDto> getAllOrders() {
        return orderService.findAll();
    }
}
