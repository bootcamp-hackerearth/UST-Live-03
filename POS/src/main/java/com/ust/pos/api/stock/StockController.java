package com.ust.pos.api.stock;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stock.service.StockService;
import com.ust.pos.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("stockApiController")
@RequestMapping("/api/stocks")
public class StockController extends BaseController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/list")
    public ResponseEntity<List<StockDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<StockDto> stocks = stockService.findAll(pageable);
        return ResponseEntity.ok(stocks);
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

    @PostMapping("/update-quantity/{stockId}")
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

    @PostMapping("/delete/{id}")
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