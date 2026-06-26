package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderControllerApi extends BaseController {

    private final OrderService orderService;

    public OrderControllerApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/list")
    public WsDto<OrderDto> list(@RequestBody PaginationDto pagination) {

        Pageable pageable = getPageable(
                pagination.getPage(),
                pagination.getSizePerPage(),
                pagination.getSortDirection(),
                pagination.getSortfield()
        );

        return orderService.findAll(pageable);
    }

   @PostMapping("/checkout")
    public OrderDto checkout(@RequestBody OrderDto orderDto) {
        return orderService.checkout(orderDto);
    }

    @GetMapping("/get")
    public OrderDto get(@RequestParam String identifier) {
        return orderService.get(identifier);
    }

    @GetMapping("/search")
    public List<OrderDto> search(@RequestParam String query) {
        return orderService.search(query);
    }
}