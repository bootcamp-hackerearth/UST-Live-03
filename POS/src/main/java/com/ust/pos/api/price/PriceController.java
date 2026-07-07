package com.ust.pos.api.price;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<PriceDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Price> spec = buildGlobalSearchSpec(Price.class, paginationDto.getKeyword());
            return priceService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return priceService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<PriceDto> getById(@PathVariable Long id) {
        PriceDto response = priceService.getPriceById(id);
        if (response == null || !response.isSuccess()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('Admin')")
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
    @PreAuthorize("hasAuthority('Admin')")
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
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        try {
            priceService.deletePrice(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/products")
    @PreAuthorize("hasAuthority('Admin')")
    public WsDto<ProductDto> getAllProducts() {
        return productService.findAll(Pageable.unpaged());
    }
}