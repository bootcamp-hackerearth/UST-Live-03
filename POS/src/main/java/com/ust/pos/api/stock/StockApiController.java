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

    private final StockService stockService;

    public StockApiController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/list")
    public WsDto<StockDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortField()
        );

        Page<StockDto> pageResult =
                stockService.findAll(pageable, paginationDto.getSearch());

        WsDto<StockDto> output = new WsDto<>();
        output.setContent(pageResult.getContent());
        output.setPage(pageResult.getNumber());
        output.setSizePerPage(pageResult.getSize());
        output.setTotalPages(pageResult.getTotalPages());

        return output;
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @GetMapping("/get")
    public StockDto update(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
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
    public boolean toggleStatus(@RequestParam String identifier) {
        try {
            stockService.toggleStatus(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}