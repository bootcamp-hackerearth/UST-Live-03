package com.ust.pos.api.orderitem;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderItemDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.orderitem.service.OrderItemService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orderitem")
public class OrderItemApiController extends BaseController {

    private final OrderItemService orderItemService;

    public OrderItemApiController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/list")
    public List<OrderItemDto> home() {
        return orderItemService.findAll();
    }

    @PostMapping("/list")
    public WsDto<OrderItemDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField());
        WsDto<OrderItemDto> response = new WsDto<>();
        response.setContent(orderItemService.findAll(pageable));
        response.setPage(paginationDto.getPage());
        response.setSizePerPage(paginationDto.getSizePerPage());

        return response;
    }

    @PostMapping("/add")
    public OrderItemDto add(@RequestBody OrderItemDto dto) {
        return orderItemService.save(dto);
    }

    @GetMapping("/get")
    public OrderItemDto get(@RequestParam String identifier) {
        return orderItemService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public OrderItemDto update(@RequestBody OrderItemDto dto) {
        return orderItemService.update(dto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            orderItemService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/getByOrder")
    public List<OrderItemDto> getByOrder(@RequestParam String orderIdentifier) {
        return orderItemService.findByOrderIdentifier(orderIdentifier);
    }
}