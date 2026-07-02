package com.ust.pos.api.stock;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.stock.service.StockService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stock")
public class StockControllerApi extends BaseController {
    public static final String REDIRECT_STOCK_LIST = "redirect:/stock/list";

    private final StockService stockService;

    public StockControllerApi(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/list")
    public PageDto<StockDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Stock> spec = buildGlobalSearchSpec(Stock.class, paginationDto.getKeyword());
            return stockService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return stockService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public StockDto getModelByIdentifier(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @PutMapping("/update")
    public StockDto updatePost(@RequestBody StockDto stockDto) {
        return stockService.update(stockDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            stockService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        stockService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<StockDto> findByStatus() {
        return stockService.findActiveStocks();
    }
}