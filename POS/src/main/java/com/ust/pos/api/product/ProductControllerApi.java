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
public class ProductControllerApi extends BaseController {
    @Autowired
    ProductService productService;

    @PostMapping("/list")
    public WsDto<ProductDto> findAll(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return productService.findAll(pageable);

    }

    @PostMapping("/add")
    public ProductDto save(@RequestBody ProductDto productDto) {
        return productService.save(productDto);

    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam Long id) {
        try {
            productService.delete(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/update")
    public ProductDto update(@RequestParam Long id) {
        return productService.findById(id);

    }

    @PostMapping("/update")
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