package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
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
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public WsDto<ProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Product> example = buildGlobalSearchSpec(Product.class, paginationDto.getKeyword());
            if (example != null) {
                return productService.findAll(example, pageable, paginationDto.getKeyword());
            }
        }
        return productService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public ProductDto addPost(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @GetMapping("/get")
    public ProductDto update(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Manager')")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            productService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/getAllActive")
    public List<ProductDto> getAllActive() {
        return productService.findAllActive();
    }

    @PostMapping("/toggleStatus")
    public boolean toggleStatus(@RequestBody ProductDto dto) {
        try {
            productService.toggleStatus(dto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
