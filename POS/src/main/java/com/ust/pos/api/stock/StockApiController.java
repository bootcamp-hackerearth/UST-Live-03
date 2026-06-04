package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockApiController extends BaseController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/list")
    public List<StockDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return stockService.findAll(pageable);
    }

    @PostMapping("/add")
    public StockDto addPost(@RequestBody StockDto stockDto) {

        return stockService.save(stockDto);
    }

    @GetMapping("/get")
    public StockDto update(Model model, @RequestParam String identifier) {

        return stockService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public StockDto updatePost(@RequestBody StockDto stockDto) {

        return stockService.update(stockDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {

        try {
            stockService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}

