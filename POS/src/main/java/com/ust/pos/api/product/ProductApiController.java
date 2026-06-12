package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductApiController extends BaseController {

    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public WsDto<ProductDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return productService.findAll(pageable);
    }

    @PostMapping("/add")
    public ProductDto addPost(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @PostMapping("/get")
    public ProductDto update(@RequestBody String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            productService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
