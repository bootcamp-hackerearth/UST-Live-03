package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderApiController extends BaseController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/placeOrder")
    public OrderDto placeOrder(@RequestBody OrderDto orderDto) {
        return orderService.placeOrder(orderDto.getIdentifier());
    }

    @PostMapping("/getOrder")
    public OrderDto getOrder(@RequestBody OrderDto orderDto) {
        return orderService.findByIdentifier(orderDto.getIdentifier());
    }

    @PostMapping("/getOrdersByCustomer")
    public List<OrderDto> getOrdersByCustomer(@RequestBody OrderDto orderDto) {
        return orderService.findAllByCustomer(orderDto.getCustomer());
    }

    @PostMapping("/cancelOrder")
    public boolean cancelOrder(@RequestBody OrderDto orderDto) {
        try {
            orderService.cancelOrder(orderDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/list")
    public List<OrderDto> list() {
        return orderService.findAll();
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            orderService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}