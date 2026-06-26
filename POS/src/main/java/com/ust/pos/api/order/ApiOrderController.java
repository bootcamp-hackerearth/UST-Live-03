package com.ust.pos.api.order;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class ApiOrderController{

    private final OrderService orderService;

    public ApiOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/generateId")
    public String generateOrderId(@RequestBody OrderDto orderDto){
        return orderService.generateOrderId(orderDto.getIdentifier());
    }

    @PostMapping("/create")
    public OrderDto createOrder(@RequestBody OrderDto orderDto){
        return orderService.placeOrder(orderDto.getIdentifier(),orderDto.getPaymentMode());
    }

    @GetMapping("list")
    public List<OrderDto> getAllOrders(){
        return orderService.findAll();
    }
}