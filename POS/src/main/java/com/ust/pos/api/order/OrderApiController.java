package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderApiController extends BaseController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public List<OrderDto> home() {
        return orderService.findAll();
    }

    @PostMapping("/list")
    public WsDto<OrderDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField());
        Page<OrderDto> pageResult =
                orderService.findAll(pageable, paginationDto.getSearch());

        WsDto<OrderDto> response = new WsDto<>();
        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
        return response;
    }

    @PostMapping("/create")
    public OrderDto createOrder(
            @RequestParam String cartId,
            @RequestParam String paymentMethod) {
        return orderService.createOrder(cartId, paymentMethod);
    }

    @GetMapping("/get")
    public OrderDto get(@RequestParam String identifier) {

        return orderService.findByIdentifier(identifier);
    }

    @PutMapping("/updateStatus")
    public OrderDto updateStatus(
            @RequestParam String orderId,
            @RequestParam String status) {

        return orderService.updateStatus(orderId, status);
    }

    @PutMapping("/update")
    public OrderDto update(@RequestBody OrderDto dto) {
        return orderService.updateStatus(
                dto.getIdentifier(),
                dto.getOrderStatus().name());
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
}