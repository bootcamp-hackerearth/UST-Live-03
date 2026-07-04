package com.ust.pos.api.orderentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.orderentry.OrderEntryService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orderentry")
public class OrderEntryApiController extends BaseController {

    private final OrderEntryService orderEntryService;

    public OrderEntryApiController(OrderEntryService orderEntryService) {
        
        this.orderEntryService = orderEntryService;
    }

    @PostMapping("/list")
    public WsDto<OrderEntryDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<OrderEntry> example = buildGlobalSearchSpec(OrderEntry.class, paginationDto.getSearch());
            if (example != null) {
                return orderEntryService.findAll(example, pageable);
            }
        }
        return orderEntryService.findAll(pageable);
    }

    @PostMapping("/get")
    public List<OrderEntryDto> home(@RequestBody String orderId) {

        return orderEntryService.findOrderEntryByOrderId(orderId);
    }
}
