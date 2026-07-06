package com.ust.pos.api.order;

import com.ust.pos.base.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Order;
import com.ust.pos.order.service.OrderService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderApiController extends BaseController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public OrderDto checkout(@RequestBody OrderDto orderDto) {
        return orderService.checkout(orderDto);
    }

    @GetMapping("/get")
    public OrderDto get(@RequestParam String identifier) {
        return orderService.get(identifier);
    }

    @PostMapping("/list")
    public WsDto<OrderDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Order> example = buildGlobalSearchSpec(Order.class, paginationDto.getKeyword());
            if (example != null) {
                return orderService.findAll(example, pageable);
            }
        }

        return orderService.findAll(pageable);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            return orderService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
    }

}
