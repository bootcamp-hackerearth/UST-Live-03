package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderControllerApi extends BaseController {

    private final OrderService orderService;

    @PostMapping("/place")
    public OrderDto placeOrder(@RequestBody OrderDto orderDto) {
        return orderService.placeOrder(orderDto.getIdentifier(), orderDto.getPaymentMethod());
    }

    @PostMapping("/list")
    public PaginatedResponseDto<OrderDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return orderService.findAll(pageable);
    }

    @GetMapping("/get")
    public OrderDto get(@RequestParam String identifier) {
        return orderService.findByIdentifier(identifier);
    }

    @GetMapping("/getById")
    public OrderDto getById(@RequestParam Long id) {
        return orderService.findById(id);
    }
}