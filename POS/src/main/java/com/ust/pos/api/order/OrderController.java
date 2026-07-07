package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PlaceOrderRequestDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Orders;
import com.ust.pos.order.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("orderApiController")
@RequestMapping("/api/orders")
public class OrderController extends BaseController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderRequestDto request) {
        OrderDto response = orderService.placeOrder(request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<OrderDto> getByIdentifier(@PathVariable String identifier) {
        OrderDto response = orderService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<OrderDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Orders> spec = buildGlobalSearchSpec(Orders.class, paginationDto.getKeyword());
            return orderService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return orderService.findAll(pageable);
    }

    @GetMapping("/customer/{customerIdentifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<List<OrderDto>> getByCustomer(@PathVariable String customerIdentifier) {
        List<OrderDto> orders = orderService.findByCustomerIdentifier(customerIdentifier);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/delete/{identifier}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        boolean response = orderService.delete(identifier);
        return ResponseEntity.ok(response);
    }
}