package com.ust.pos.api.orderapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.Order;
import com.ust.pos.order.service.OrderService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class ApiOrderController extends BaseController {

    public ApiOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private final OrderService orderService;

    @PostMapping("/place")
    public OrderDto placeOrder(@RequestBody OrderDto orderDto) {
        return orderService.placeOrder(orderDto.getIdentifier(), orderDto.getPaymentMode());
    }

    @PostMapping("/getOrder")
    public OrderDto getOrder(@RequestBody OrderDto orderDto) {
        return orderService.findByOrderId(orderDto.getOrderId());
    }

    @PostMapping("/list")
    public WsDto<OrderDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Order> example = buildGlobalSearchSpec(Order.class, paginationDto.getKeyword());
            if (example != null) {
                return orderService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return orderService.findAll(pageable);
    }
}