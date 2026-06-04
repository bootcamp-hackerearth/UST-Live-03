package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/product/list";

    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public List<ProductDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return productService.findAll(pageable);
    }

    @PostMapping("/add")
    public ProductDto addPost(@RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @GetMapping("/get")
    public ProductDto updatePage(@RequestParam String identifier) {
        return productService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {
        return productService.update(productDto);
    }

    @PostMapping("/delete")
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

    @PostMapping("/toggle")
    public ProductDto toggleStatus(@RequestBody ProductDto productDto) {
        return productService.toggleStatus(productDto.getIdentifier());
    }

    @GetMapping("/active")
    public List<ProductDto> getActiveProducts() {
        return productService.findActiveProducts();
    }
}