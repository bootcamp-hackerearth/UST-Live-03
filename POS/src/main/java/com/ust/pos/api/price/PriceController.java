package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("priceApiController")
@RequestMapping("/api/prices")
public class PriceController extends BaseController {
    private final PriceService priceService;
    private final ProductService productService;

    public PriceController(PriceService priceService, ProductService productService) {
        this.priceService = priceService;
        this.productService = productService;
    }

    @PostMapping("/list")
    public WsDto<PriceDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return priceService.findAll(pageable);
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

    @PutMapping("/update/{id}")
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        try {
            priceService.deletePrice(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/products")
    public WsDto<ProductDto> getAllProducts() {
        return productService.findAll(Pageable.unpaged());
    }
}