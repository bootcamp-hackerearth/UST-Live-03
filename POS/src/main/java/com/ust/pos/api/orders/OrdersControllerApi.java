package com.ust.pos.api.orders;

import com.ust.pos.dto.OrdersDto;
import com.ust.pos.order.service.OrderService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrdersControllerApi {

    private final OrderService orderService;

    public OrdersControllerApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public List<OrdersDto> home(){
        return orderService.findAll();
    }

    @GetMapping("/get")
    public OrdersDto update(@RequestParam String identifier) {
        return orderService.findByIdentifier(identifier);
    }
}
