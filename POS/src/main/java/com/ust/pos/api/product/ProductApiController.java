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
    public WsDto<ProductDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getSearch())) {
            Specification<Product> example = buildGlobalSearchSpec(Product.class, paginationDto.getSearch());
            if (example != null) {
                return productService.findAll(example, pageable);
            }
        }
        return productService.findAll(pageable);
    }

    @PostMapping("/cart-list")
    public WsDto<ProductDto> cartHome(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return productService.findAllWithQuantity(pageable);
//ci/cd

    }


    @PostMapping("/add")
    public ProductDto addPost(@RequestBody ProductDto userDto) {

        return productService.save(userDto);
    }

    @GetMapping("/{identifier}")
    public ProductDto update(@PathVariable String identifier) {

        identifier = identifier.replace("\"", "");
        return productService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {

        return productService.update(productDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody ProductDto productDto) {

        String identifier = productDto.getIdentifier();
        identifier = identifier.replace("\"", "");

        try {
            productService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @PatchMapping("/toggle")
    public boolean toggleStatus(@RequestBody String identifier) {

        try {
            productService.toggleStatus(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/getactive")
    public List<ProductDto> getActiveProducts() {

        return productService.findActiveShelf();
    }

}
