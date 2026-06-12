package com.ust.pos.api.product;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/product")
public class ProductControllerApi extends BaseController {

    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public PageDto<ProductDto> product(PaginationDto paginationDto) {
        Pageable pageable=getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),paginationDto.getSortDirection(),paginationDto.getSortField());
        return productService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public ProductDto getProductByIdentifier(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public ProductDto addPost(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @PostMapping("/update")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {
        return productService.update(productDto);

    }
    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            productService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }
    @GetMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
    }
    @GetMapping("/findByStatus")
    public List<ProductDto> findByStatus() {
        return productService.findActiveProducts();
    }
}
