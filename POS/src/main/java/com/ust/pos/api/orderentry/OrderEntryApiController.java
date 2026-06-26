package com.ust.pos.api.orderentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.orderentry.service.OrderEntryService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orderentry")
public class OrderEntryApiController extends BaseController {

    private final OrderEntryService orderEntryService;

    public OrderEntryApiController(OrderEntryService orderEntryService) {
        this.orderEntryService = orderEntryService;
    }

    @PostMapping("/list")
    public WsDto<OrderEntryDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField()
        );
        return orderEntryService.findAll(pageable);
    }

    @PostMapping("/add")
    public OrderEntryDto add(@RequestBody OrderEntryDto orderEntryDto) {
        return orderEntryService.save(orderEntryDto);
    }

    @PostMapping("/get")
    public OrderEntryDto get(@RequestBody String identifier) {
        return orderEntryService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public OrderEntryDto update(@RequestBody OrderEntryDto orderEntryDto) {
        return orderEntryService.update(orderEntryDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            orderEntryService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}