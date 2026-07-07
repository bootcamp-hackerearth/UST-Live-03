package com.ust.pos.api.product;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.product.service.ProductService;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/product")
public class ProductControllerApi extends BaseController {

    private final ProductService productService;

    public ProductControllerApi(ProductService productService){
        this.productService=productService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('Admin')")
    public PageDto<ProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Product> spec = buildGlobalSearchSpec(Product.class, paginationDto.getKeyword());
            return productService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return productService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public ProductDto getProductByIdentifier(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('Admin')")
    public ProductDto addPost(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('Admin')")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {
        return productService.update(productDto);

    }
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Admin')")
    public boolean delete(@RequestParam String identifier) {
        try {
            productService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    @PreAuthorize("hasAuthority('Admin')")
    public void toggleStatus(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<ProductDto> findByStatus() {
        return productService.findActiveProducts();
    }
}
