package com.ust.pos.api.order;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PlaceOrderRequestDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderRequestDto request) {
        OrderDto response = orderService.placeOrder(request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<OrderDto> getByIdentifier(@PathVariable String identifier) {
        OrderDto response = orderService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/list")
    public ResponseEntity<List<OrderDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );
        List<OrderDto> orders = orderService.findAll(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerIdentifier}")
    public ResponseEntity<List<OrderDto>> getByCustomer(@PathVariable String customerIdentifier) {
        List<OrderDto> orders = orderService.findByCustomerIdentifier(customerIdentifier);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        boolean response = orderService.delete(identifier);
        return ResponseEntity.ok(response);
    }
}