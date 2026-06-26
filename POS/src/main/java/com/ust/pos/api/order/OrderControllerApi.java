package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderControllerApi extends BaseController {

    private final OrderService orderService;

    public OrderControllerApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all")
    public List<OrderDto> all() {
        return orderService.findAll();
    }

    @PostMapping("/list")
    public WsDto<OrderDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField());

        Page<OrderDto> pageResult = orderService.findAll(pageable, paginationDto.getSearch());

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
    public OrderDto updateStatus(@RequestParam String orderId, @RequestParam String status) {
        return orderService.updateStatus(orderId, status);
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