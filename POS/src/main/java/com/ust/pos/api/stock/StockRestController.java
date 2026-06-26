package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.stock.service.StockService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/stock")
public class StockRestController extends BaseController {

    private final StockService stockService;

    public StockRestController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/list")
    public WsDto<StockDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @PostMapping("/get")
    public StockDto update(@RequestBody String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public StockDto updatePost(@RequestBody StockDto stockDto) {
        return stockService.update(stockDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            stockService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    public String toggleStatus(@RequestBody String identifier) {
        stockService.toggleStatus(identifier);
        return identifier;
    }
}
