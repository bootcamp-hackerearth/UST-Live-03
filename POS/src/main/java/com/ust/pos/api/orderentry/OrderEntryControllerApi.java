package com.ust.pos.api.orderentry;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.orderentry.service.OrderEntryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderEntry")
public class OrderEntryControllerApi {
    private final OrderEntryService orderEntryService;

    public OrderEntryControllerApi(OrderEntryService orderEntryService) {
        this.orderEntryService = orderEntryService;
    }

    @PostMapping("/add")
    public OrdersDto addPost(@RequestBody OrdersDto ordersDto) {
        return orderEntryService.save(ordersDto);
    }

    @GetMapping("findByOrderId")
    public List<OrderEntryDto> findByOrderId(@RequestParam String orderId){
        return orderEntryService.findByOrderId(orderId);
    }
}
