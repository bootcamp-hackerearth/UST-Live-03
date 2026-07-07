package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderApiController extends BaseController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public OrderDto checkout(@RequestBody OrderDto orderDto) {
        return orderService.checkout(orderDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('Supervisor')")
    public OrderDto get(@RequestParam String identifier) {
        return orderService.get(identifier);
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Admin','Supervisor')")
    public PaginationResponseDto<OrderDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return orderService.findAll(pageable);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Supervisor')")
    public boolean delete(@RequestParam String identifier) {
        try {
            return orderService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
    }
}