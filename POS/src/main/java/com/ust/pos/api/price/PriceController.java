package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("priceApiController")
@RequestMapping("/api/prices")
public class PriceController extends BaseController {

    @Autowired
    private PriceService priceService;

    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public ResponseEntity<List<PriceDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<PriceDto> prices = priceService.findAll(pageable);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceDto> getById(@PathVariable Long id) {
        PriceDto response = priceService.getPriceById(id);
        if (response == null || !response.isSuccess()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<PriceDto> save(@RequestBody PriceDto priceDto) {
        try {
            PriceDto response = priceService.createPrice(priceDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            PriceDto errorResponse = new PriceDto();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<PriceDto> update(@PathVariable Long id, @RequestBody PriceDto priceDto) {
        try {
            priceDto.setId(id);
            PriceDto response = priceService.updatePrice(priceDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            PriceDto errorResponse = new PriceDto();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        try {
            priceService.deletePrice(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.findAll(null);
        return ResponseEntity.ok(products);
    }
}