package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Order;
import com.ust.pos.order.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/order")
@RestController
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'CASHIER', 'SUPPORT', 'ACCOUNTANT')")
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
    public WsDto<OrderDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Order> example = buildGlobalSearchSpec(Order.class, paginationDto.getKeyword());
            if (example != null) {
                return orderService.findAll(example, pageable);
            }
        }

        return orderService.findAll(pageable);
    }
}