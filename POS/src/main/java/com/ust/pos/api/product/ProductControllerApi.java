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
public class ProductControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/product/list";

    private final ProductService productService;

    public ProductControllerApi(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public WsDto<ProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortfield());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Product> example = buildGlobalSearchSpec(Product.class, paginationDto.getKeyword());
            if (example != null) {
                return productService.findAll(example, pageable);
            }
        }

        return productService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public ProductDto addPost(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public ProductDto updatePage(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public ProductDto delete(@RequestBody ProductDto productDto) {
        ProductDto response = new ProductDto();
        try {
            productService.delete(productDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Product deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('Manager','Admin')")
    public ProductDto toggleStatus(@RequestBody ProductDto productDto) {
        return productService.toggleStatus(productDto.getIdentifier());
    }

    @GetMapping("/active")
    public List<ProductDto> getActiveProducts() {
        return productService.findActiveProducts();
    }

    @GetMapping("/search")
    public List<ProductDto>searchProduct(@RequestParam String query){
        return productService.searchProduct(query);
    }
}