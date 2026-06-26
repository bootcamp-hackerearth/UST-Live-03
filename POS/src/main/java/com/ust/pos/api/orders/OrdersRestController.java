package com.ust.pos.api.orders;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.orders.service.OrdersService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrdersRestController extends BaseController {

    private final OrdersService ordersService;

    public OrdersRestController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping("/list")
    public WsDto<OrdersDto> home(
            @RequestBody PaginationDto paginationDto) {

        Pageable pageable =
                getPageable(
                        paginationDto.getPage(),
                        paginationDto.getSizePerPage(),
                        paginationDto.getSortDirection(),
                        paginationDto.getSortField()
                );

        return ordersService.findAll(pageable);
    }

    @PostMapping("/add")
    public OrdersDto addPost(
            @RequestBody OrdersDto ordersDto) {

        return ordersService.save(ordersDto);
    }

    @PostMapping("/get")
    public OrdersDto get(
            @RequestBody String identifier) {

        return ordersService.get(identifier);
    }


    @DeleteMapping("/delete")
    public boolean delete(
            @RequestBody String identifier) {
        try {
            ordersService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}