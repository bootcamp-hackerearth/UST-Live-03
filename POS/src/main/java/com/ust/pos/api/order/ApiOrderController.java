package com.ust.pos.api.order;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class ApiOrderController {

    private final OrderService orderService;

    public ApiOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> handleCheckoutRequest(@RequestBody OrderDto incomingOrderDto) {
        try {
            OrderDto processedOrder = orderService.processCheckout(incomingOrderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(processedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> listOrders(@RequestBody Map<String, Object> searchParams) {
        try {
            List<OrderDto> ordersList = orderService.getAllOrdersList();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("dtoList", ordersList);
            responseMap.put("totalPages", 1);
            responseMap.put("totalRecords", ordersList.size());

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<OrderDto> getInvoiceReceiptDetails(@PathVariable String identifier) {
        try {
            OrderDto trackingDetails = orderService.getOrderDetails(identifier);
            return ResponseEntity.ok(trackingDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}