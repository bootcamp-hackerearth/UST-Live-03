package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.*;
import com.ust.pos.model.Stock;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("stockApiController")
@RequestMapping("/api/stocks")
public class StockController extends BaseController {
    private final StockService stockService;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public StockController(StockService stockService, ProductService productService, WarehouseService warehouseService) {
        this.stockService = stockService;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    @PostMapping("/list")
    public WsDto<StockDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Stock> spec = buildGlobalSearchSpec(Stock.class, paginationDto.getKeyword());
            return stockService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return stockService.findAll(pageable);
    }

    @GetMapping("/search")
    public ResponseEntity<StockDto> get(@RequestParam Long productId, @RequestParam Long warehouseId) {
        StockDto response = stockService.getStock(productId, warehouseId);
        if (response == null || !response.isSuccess()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<StockDto> save(@RequestBody StockDto stockDto) {
        StockDto response = stockService.createStock(stockDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-quantity/{stockId}")
    public ResponseEntity<StockDto> updateQuantity(@PathVariable Long stockId, @RequestParam Integer quantity) {
        StockDto response = stockService.updateStockQuantity(stockId, quantity);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/toggle-status")
    public ResponseEntity<Boolean> toggleStatus(@RequestParam Long id) {
        try {
            stockService.toggleStatus(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        try {
            boolean deleted = stockService.deleteStock(id);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getProducts() {
        List<ProductDto> products = productService.findIfTrue();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseDto>> getWarehouses() {
        List<WarehouseDto> warehouses = warehouseService.findIfTrue();
        return ResponseEntity.ok(warehouses);
    }
}