package com.ust.pos.api.ordersentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrdersEntryDto;
import com.ust.pos.ordersentry.service.OrdersEntryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordersentry")
public class OrdersEntryRestController extends BaseController {

    private final OrdersEntryService ordersEntryService;

    public OrdersEntryRestController(OrdersEntryService ordersEntryService) {
        this.ordersEntryService = ordersEntryService;
    }

    @PostMapping("/add")
    public OrdersEntryDto addPost(
            @RequestBody OrdersEntryDto ordersEntryDto) {

        return ordersEntryService.save(
                ordersEntryDto
        );
    }

    @PostMapping("/get")
    public OrdersEntryDto get(
            @RequestBody String identifier) {

        return ordersEntryService.get(
                identifier
        );
    }

    @DeleteMapping("/delete")
    public boolean delete(
            @RequestBody String identifier) {

        try {
            ordersEntryService.delete(
                    identifier
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/findbyordersid")
    public List<OrdersEntryDto> findByOrdersId(
            @RequestBody String ordersId) {

        return ordersEntryService.findByOrdersId(
                ordersId
        );
    }
}