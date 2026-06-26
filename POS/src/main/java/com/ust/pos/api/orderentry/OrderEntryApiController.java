package com.ust.pos.api.orderentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.orderentry.service.OrderEntryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderentry")
public class OrderEntryApiController extends BaseController {


    private final OrderEntryService orderEntryService;

    public OrderEntryApiController(OrderEntryService orderEntryService) {
        this.orderEntryService = orderEntryService;
    }

    @PostMapping("/getByOrderId")
    public List<OrderEntryDto> getByOrderId(@RequestBody String orderId) {
        return orderEntryService.findByOrderId(orderId);
    }

    @DeleteMapping("/deleteByOrderId")
    public boolean deleteByOrderId(@RequestBody String orderId) {
        try {
            orderEntryService.deleteByOrderId(orderId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}