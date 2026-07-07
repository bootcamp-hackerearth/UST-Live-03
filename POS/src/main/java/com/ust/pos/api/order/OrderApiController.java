package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@PreAuthorize("hasAnyAuthority('Admin', 'Cashier')")
public class OrderApiController
        extends BaseController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/list")
    public WsDto<OrderDto> list(
            @RequestBody
            PaginationDto paginationDto
    ) {

        Pageable pageable =
                getPageable(
                        paginationDto.getPage(),
                        paginationDto.getSizePerPage(),
                        paginationDto.getSortDirection(),
                        paginationDto.getSortField()
                );

        Page<OrderDto> pageResult = orderService.findAll(paginationDto.getSearch(), pageable);

        WsDto<OrderDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }

    @PostMapping("/add")
    public OrderDto add(
            @RequestBody
            OrderDto orderDto
    ) {
        return orderService.save(
                orderDto
        );
    }
    @PostMapping("/placeOrder")
    public OrderDto placeOrder(
            @RequestBody OrderDto orderDto
    )
    {
        return orderService.placeOrder(
                orderDto
        );
    }

    @GetMapping("/getByCustomer")
    public List<OrderDto> getByCustomer(
            @RequestParam String email
    )
    {
        return orderService.findByCustomerEmail(
                email
        );
    }

    @GetMapping("/get")
    public OrderDto get(
            @RequestParam
            String identifier
    ) {

        return orderService
                .findByIdentifier(
                        identifier
                );
    }

    @PutMapping("/update")
    public OrderDto update(
            @RequestBody
            OrderDto orderDto
    ) {

        return orderService.update(
                orderDto
        );
    }

    @DeleteMapping("/delete")
    public Boolean delete(
            @RequestParam
            String identifier
    ) {

        try {

            orderService.delete(
                    identifier
            );

        } catch(Exception e) {

            return false;
        }

        return true;
    }
}