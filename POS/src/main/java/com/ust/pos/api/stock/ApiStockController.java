package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class ApiStockController extends BaseController {

    @Autowired
    StockService stockService;

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    ProductService productService;

    @PostMapping("/list")
    public List<StockDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable= getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto add(@RequestBody StockDto stockDto) {
        return stockService.save(stockDto);
    }

    @GetMapping("/delete")
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
    public StockDto update(@RequestParam String identifier) {
        return stockService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public StockDto updatePost(@RequestBody StockDto stockDto) {
        return stockService.update(stockDto);
    }

    @PostMapping("/toggle")
    public StockDto toggle(@RequestBody StockDto stockDto) {
        return stockService.changeToggleStatus(stockDto.getIdentifier(), stockDto.isStatus());
    }
}
