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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockApiController extends BaseController {

    private final StockService stockService;

    public StockApiController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/list")
    public WsDto<StockDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<Stock> example = buildGlobalSearchSpec(Stock.class, paginationDto.getSearch());
            if (example != null) {
                return stockService.findAll(example, pageable);
            }
        }
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {

        return stockService.save(stockDto);
    }

    @GetMapping("/{identifier}")
    public StockDto update(@PathVariable String identifier) {

        return stockService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public StockDto updatePost(@RequestBody StockDto stockDto) {

        return stockService.update(stockDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody StockDto stockDto) {

        String identifier = stockDto.getIdentifier();

        try {
            stockService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

