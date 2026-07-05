package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.product.service.ProductService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductApiController extends BaseController {
    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT','SUPPORT')")
    public WsDto<ProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage()
                , paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Product> example = buildGlobalSearchSpec(Product.class, paginationDto.getKeyword());
            if (example != null) {
                return productService.findAll(example, pageable);
            }
        }
        return productService.findAll(pageable);
    }

    @GetMapping("getAllActive")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT','SUPPORT','CASHIER','STOCK_MANAGER')")
    public List<ProductDto> getAllActive() {
        return productService.findAllActive();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ProductDto add(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ACCOUNTANT','SUPPORT')")
    public ProductDto getByIdentifier(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ProductDto update(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try {
            productService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','SUPPORT')")
    public ProductDto toggleStatus(@RequestParam String identifier) {
        return productService.toggleStatus(identifier);
    }
}
