package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.stock.service.StockService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/stock")
public class ApiStockController extends BaseController {

    private final StockService stockService;

    public ApiStockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public WsDto<StockDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Stock> example = buildGlobalSearchSpec(Stock.class, paginationDto.getKeyword());
            if (example != null) {
                return stockService.findAll(example, pageable);
            }
        }
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public StockDto addPost(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try{
            stockService.delete(identifier);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public StockDto update(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public StockDto updatePost(@RequestBody StockDto stockDto) {
        return stockService.update(stockDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public StockDto toggle(@RequestBody StockDto stockDto) {
        return stockService.changeToggleStatus(stockDto.getIdentifier(), stockDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STOCK_MANAGER')")
    public List<StockDto> findActive() {
        return stockService.findActiveStatus();
    }
}
