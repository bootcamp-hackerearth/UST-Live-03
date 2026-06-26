package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductControllerApi extends BaseController {

    private final ProductService productService;

    @PostMapping("/list")
    public PaginatedResponseDto<ProductDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return productService.findAll(pageable);
    }

    @PostMapping("/add")
    public ProductDto addPost(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @GetMapping("/get")
    public ProductDto get(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @DeleteMapping("/delete")
    public ProductDto delete(@RequestBody ProductDto productDto) {
        try {
            return productService.delete(productDto.getIdentifier());
        } catch (Exception e) {
            ProductDto errorDto = new ProductDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody ProductDto productDto) {
        try {
            productService.changeStatus(productDto.getIdentifier(), productDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/active")
    public List<ProductDto> getActiveProducts() {
        return productService.findAllActive();
    }
}