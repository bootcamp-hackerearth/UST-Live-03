package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ApiProductController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/product/list";

    private final ProductService productService;

    public ApiProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/list")
    public WsDto<ProductDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Product> example = buildGlobalSearchSpec(Product.class, paginationDto.getKeyword());
            if (example != null) {
                return productService.findAll(example, pageable);
            }
        }
        return productService.findAll(pageable);
    }


    @PostMapping("/add")
    public ProductDto addPost(@RequestBody ProductDto productDto) {

        return productService.save(productDto);

    }

    @GetMapping("/get")
    public ProductDto update(@RequestParam String identifier) {

        return productService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public ProductDto updatePost(@RequestBody ProductDto userDto) {

        return productService.update(userDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            productService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public ProductDto toggle(@RequestParam String identifier) {

        return productService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<ProductDto> findByStatus() {

        return productService.findIfTrue();
    }
}
