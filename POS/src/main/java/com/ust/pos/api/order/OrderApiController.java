package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/order")
@RestController
public class OrderApiController extends BaseController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public OrderDto placeOrder(@RequestBody OrderDto orderDto) {
        return orderService.placeOrder(orderDto.getIdentifier(), orderDto.getPaymentMode());
    }

    @PostMapping("/get")
    public OrderDto getOrder(@RequestBody OrderDto orderDto) {
        return orderService.findByOrderId(orderDto.getOrderId());
    }

    @PostMapping("/list")
    public WsDto<OrderDto> listOrders(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortDirection(),
                paginationDto.getSortField());
        return orderService.findAll(pageable);
    }
}