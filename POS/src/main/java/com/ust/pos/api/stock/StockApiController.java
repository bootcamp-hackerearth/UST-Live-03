package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockApiController extends BaseController {

    @Autowired
    private StockService stockService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public WsDto<StockDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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
    public boolean delete(@RequestBody StockDto stockDto) {
        try {
            stockService.delete(stockDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/toggleStatus")
    public StockDto toggleStatus(@RequestBody StockDto stockDto) {
        return stockService.toggleStatus(stockDto.getIdentifier(), stockDto.isStatus());
    }
}