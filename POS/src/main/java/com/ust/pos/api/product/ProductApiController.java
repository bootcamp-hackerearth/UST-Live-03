package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductApiController extends BaseController {

    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public WsDto<ProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return productService.findAll(pageable);
    }

    @PostMapping("/add")
    public ProductDto add(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @GetMapping("/get")
    public ProductDto get(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ProductDto update(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody ProductDto productDto) {
        try {
            productService.delete(productDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/product")
    public List<ProductDto> getActiveProduct() {
        return productService.findActiveProducts();
    }

    @PostMapping("/toggleStatus")
    public ProductDto toggleStatus(@RequestBody ProductDto productDto) {
        return productService.toggleStatus(productDto.getIdentifier(), productDto.isStatus());
    }
}