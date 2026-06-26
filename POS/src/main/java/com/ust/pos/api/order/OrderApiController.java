package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderApiController extends BaseController {


    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/list")
    public WsDto<OrderDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable =
                getPageable(
                        paginationDto.getPage(),
                        paginationDto.getSizePerPage(),
                        paginationDto.getSortDirection(),
                        paginationDto.getSortField()
                );

        return orderService.findAll(pageable);
    }

    @PostMapping("/add")
    public OrderDto add(@RequestBody OrderDto orderDto) {

        return orderService.save(orderDto);
    }

    @PostMapping("/get")
    public OrderDto get(@RequestBody String identifier) {

        return orderService.get(identifier);
    }

    @PostMapping("/checkout")
    public OrderDto checkout(@RequestBody String cartId) {
        return orderService.checkout(cartId);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody String identifier) {

        try {
            orderService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}