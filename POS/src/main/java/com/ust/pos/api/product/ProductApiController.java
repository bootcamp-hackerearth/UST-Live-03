package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.product.service.ProductService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
public class ProductApiController extends BaseController {
    private final ProductService productService;


    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/list")
    public WsDto<ProductDto> home(@RequestBody PaginationDto paginationDto) throws Exception
    {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        Example<Product> example = buildSearchProbe(Product.class, paginationDto.getSearch());
        Page<ProductDto> pageResult = productService.findAll(example, pageable);

        WsDto<ProductDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }

    @GetMapping("/list")
    public List<ProductDto> dropdown()
    {
        return productService.findAll();
    }
    @PostMapping("/add")
    public ProductDto addPost(@RequestBody ProductDto productDto)
    {
        return productService.save(productDto);
    }

    @GetMapping("/get")
    public ProductDto update(@RequestParam String identifier)
    {
        return productService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ProductDto doupdate(@RequestBody ProductDto productDto)
    {
        return productService.update(productDto);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String identifier)
    {
        try {
            productService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
