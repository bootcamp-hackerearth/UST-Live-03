package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Orders;
import com.ust.pos.order.service.OrderService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderRestController extends BaseController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public OrdersDto placeOrder(@RequestBody OrdersDto ordersDto) {
        return orderService.save(ordersDto);
    }

    @PostMapping("/list")
    public WsDto<OrdersDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Orders> example = buildGlobalSearchSpec(Orders.class, paginationDto.getKeyword());
            if (example != null) {
                return orderService.findAll(example, pageable);
            }
        }

        return orderService.findAll(pageable);
    }

    @GetMapping("/get")
    public OrdersDto get(@RequestParam String identifier) {
        return orderService.findByIdentifier(identifier);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            orderService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/entries")
    public OrdersDto getEntries(@RequestParam String orderId) {
        return (OrdersDto) orderService.getOrderEntries(orderId);
    }
}