package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockControllerApi extends BaseController {

    @Autowired
    private StockService stockService;

    @PostMapping("/list")
    public List<StockDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto add(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @GetMapping("/get")
    public StockDto get(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public StockDto update(@RequestBody StockDto stockDto) {
        return stockService.update(stockDto);
    }

    @PostMapping("/delete")
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

    @GetMapping("/toggle")
    public StockDto toggle(@RequestParam String identifier) {
        return stockService.toggleStatus(identifier);
    }
}