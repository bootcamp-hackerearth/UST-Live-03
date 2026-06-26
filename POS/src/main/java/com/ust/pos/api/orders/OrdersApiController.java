package com.ust.pos.api.orders;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.orders.OrdersService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrdersApiController extends BaseController {

    private final OrdersService ordersService;

    public OrdersApiController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping("/list")
    public WsDto<OrdersDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return ordersService.findAll(pageable);
    }

    @PostMapping("/add")
    public OrdersDto addPost(@RequestBody OrdersDto ordersDto) {

        return ordersService.save(ordersDto);
    }

    @PostMapping("/get")
    public OrdersDto update(@RequestBody String identifier) {

        return ordersService.findByIdentifier(identifier);
    }
}
