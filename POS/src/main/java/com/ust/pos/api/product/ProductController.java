package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.*;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("productApiController")
@RequestMapping("/api/products")
public class ProductController extends BaseController {
    private final ProductService productService;
    private final PriceService priceService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, PriceService priceService, CategoryService categoryService) {
        this.productService = productService;
        this.priceService = priceService;
        this.categoryService = categoryService;
    }

    @PostMapping("/list")
    public WsDto<ProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(
                paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),
                paginationDto.getSortField());
        return productService.findAll(pageable);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<ProductDto> getByIdentifier(@PathVariable String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<ProductDto> save(@RequestBody ProductDto productDto) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{identifier}")
    public ResponseEntity<ProductDto> update(@PathVariable String identifier, @RequestBody ProductDto productDto) {
        productDto.setIdentifier(identifier);
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            boolean deleted = productService.delete(identifier);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{identifier}")
    public ResponseEntity<ProductDto> toggleStatus(@PathVariable String identifier) {
        ProductDto response = productService.toggleStatus(identifier);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductDto>> activeProducts() {
        List<ProductDto> activeProducts = productService.findIfTrue();
        return ResponseEntity.ok(activeProducts);
    }

    @GetMapping("/prices")
    public ResponseEntity<WsDto<PriceDto>> getPrices() {
        return ResponseEntity.ok(priceService.findAll(Pageable.unpaged()));
    }

    @GetMapping("/categories")
    public ResponseEntity<WsDto<CategoryDto>> getCategories() {
        return ResponseEntity.ok(categoryService.findAll(Pageable.unpaged()));
    }
}