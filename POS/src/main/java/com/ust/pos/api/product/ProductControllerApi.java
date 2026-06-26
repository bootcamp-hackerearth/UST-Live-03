package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductControllerApi extends BaseController {

    private final ProductService productService;

    public ProductControllerApi(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/list")
    public WsDto<ProductDto> findAll(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return productService.findAll(pageable);

    }

    @PostMapping("/add")
    public ProductDto save(@RequestBody ProductDto productDto) {
        return productService.save(productDto);

    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            productService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/update")
    public ProductDto update(@RequestParam Long id) {
        return productService.findById(id);

    }

    @PutMapping("/update")
    public ProductDto update(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @PostMapping("/changeStatus")
    public ProductDto toggle(@RequestBody ProductDto productDto) {
        return productService.changeProductStatus(productDto.getIdentifier(), productDto.isStatus());

    }

    @GetMapping("/findAllActive")
    public List<ProductDto> allactive() {
        return productService.findAllActiveProduct();
    }
}