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


@RestController
@RequestMapping("/api/stock")
public class StockControllerApi extends BaseController {

    private final StockService stockService;

    public StockControllerApi(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<StockDto> listStock(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Stock> example = buildGlobalSearchSpec(Stock.class, paginationDto.getKeyword());
            if (example != null) {
                return stockService.findAll(example, pageable);
            }
        }
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public StockDto saveStock(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }


    @GetMapping("/update")
    public StockDto showEditPage(@RequestParam String identifier) {

        return stockService.findByIdentifier(identifier);

    }

    @PutMapping("/update")
    public StockDto saveEditedStock(@RequestBody StockDto stockDto) {

        return stockService.update(stockDto);
    }


    @GetMapping("/delete")
    public boolean deleteStock(@RequestParam String identifier) {
        try {
            stockService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @DeleteMapping("/toggle")
    public boolean toggle(@RequestParam String identifier, boolean status) {
        try {
            stockService.changeStockStatus(identifier, status);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/changeStatus")
    public StockDto changestatus(@RequestBody StockDto stockDto) {
        return stockService.changeStockStatus(stockDto.getIdentifier(), stockDto.isStatus());
    }
}