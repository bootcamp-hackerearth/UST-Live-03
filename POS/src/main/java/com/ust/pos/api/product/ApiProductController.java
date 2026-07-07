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
public class ApiProductController extends BaseController {

    private final ProductService productService;

    public ApiProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','SUPPORT')")
    public WsDto<ProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Product> example = buildGlobalSearchSpec(Product.class, paginationDto.getKeyword());
            if (example != null) {
                return productService.findAll(example, pageable);
            }
        }
        return productService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','SUPPORT')")
    public ProductDto add(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
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

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','SUPPORT')")
    public ProductDto update(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','SUPPORT')")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','SUPPORT')")
    public ProductDto toggle(@RequestBody ProductDto productDto) {
        return productService.changeToggleStatus(productDto.getIdentifier(), productDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','SUPPORT')")
    public List<ProductDto> findActive() {
        return productService.findActiveStatus();
    }
}
