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
public class StockControllerApi extends BaseController {

    private final StockService stockService;

    public StockControllerApi(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WsDto<StockDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortfield());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Stock> example = buildGlobalSearchSpec(Stock.class, paginationDto.getKeyword());
            if (example != null) {
                return stockService.findAll(example, pageable);
            }
        }

        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public StockDto add(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public StockDto get(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public StockDto update(@RequestBody StockDto stockDto) {
        return stockService.update(stockDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public StockDto delete(@RequestBody StockDto stockDto) {
        StockDto response = new StockDto();
        try {
            stockService.delete(stockDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Stock deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public StockDto toggle(@RequestBody StockDto stockDto) {
        return stockService.toggleStatus(stockDto.getIdentifier());
    }

    @PostMapping("/active")
    public List<StockDto> active(){
        return stockService.findActiveStock();
    }
}