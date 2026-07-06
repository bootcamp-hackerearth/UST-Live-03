package com.ust.pos.api.stocksapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StocksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stocks;
import com.ust.pos.stocks.service.StocksService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StocksApiController extends BaseController {

    private final StocksService stocksService;

    public StocksApiController(StocksService stocksService) {
        this.stocksService = stocksService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','MANAGER','ACCOUNTANT')")
    public WsDto<StocksDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Stocks> example = buildGlobalSearchSpec(Stocks.class, paginationDto.getKeyword());
            if (example != null) {
                return stocksService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return stocksService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','MANAGER')")
    public StocksDto addPost(@RequestBody StocksDto stocksDto) {
        return stocksService.save(stocksDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','MANAGER','ACCOUNTANT')")
    public StocksDto update(@RequestParam String identifier) {
        return stocksService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','MANAGER')")
    public StocksDto updatePost(@RequestBody StocksDto stocksDto) {
        return stocksService.update(stocksDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN')")
    public boolean delete(@RequestParam String identifier) {
        try {
            stocksService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','MANAGER')")
    public StocksDto toggle(@RequestParam String identifier) {
        return stocksService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    @PreAuthorize("hasAnyAuthority('INVENTORY_MANAGER','ADMIN','MANAGER','ACCOUNTANT')")
    public List<StocksDto> findByStatus() {
        return stocksService.findIfTrue();
    }
}