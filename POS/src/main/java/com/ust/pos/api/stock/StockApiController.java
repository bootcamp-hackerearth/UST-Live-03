package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.stock.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockApiController extends BaseController {
    public static final String ADD_STOCK = "stock/add";

    private final StockService stockService;

    public StockApiController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/list")
    public WsDto<StockDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortField());
        Page<StockDto> stock = stockService.findAll(pageable, paginationDto.getSearch());
        WsDto<StockDto> result = new WsDto<>();
        result.setContent(stock.getContent());
        result.setSizePerPage(stock.getSize());
        result.setPage(stock.getNumber());
        result.setTotalPages(stock.getTotalPages());
        return result;
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @GetMapping("/get")
    public StockDto get(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public StockDto update(@RequestBody StockDto stockDto) {
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
    public boolean updateStatus(
            @RequestParam String identifier,
            @RequestParam Boolean status) {
        try {
            stockService.updateStatusOnly(identifier, status);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
