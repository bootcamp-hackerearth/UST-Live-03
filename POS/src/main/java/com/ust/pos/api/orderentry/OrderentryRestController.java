package com.ust.pos.api.orderentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.orderentry.service.OrderentryService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orderEntry")
public class OrderentryRestController extends BaseController {

    private final OrderentryService orderEntryService;

    public OrderentryRestController(OrderentryService orderEntryService) {
        this.orderEntryService = orderEntryService;
    }

    @PostMapping("/list")
    public WsDto<OrderEntryDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );

        return orderEntryService.findAll(pageable);
    }

    @GetMapping("/get")
    public OrderEntryDto get(@RequestParam String identifier) {
        return orderEntryService.findByIdentifier(identifier);
    }

    @GetMapping("/byOrder")
    public OrderEntryDto getByOrder(@RequestParam String orderId) {
        return (OrderEntryDto) orderEntryService.findByOrderId(orderId);
    }
}