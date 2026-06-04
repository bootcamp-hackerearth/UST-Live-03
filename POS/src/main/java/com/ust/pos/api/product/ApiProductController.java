package com.ust.pos.api.product;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ApiProductController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/product/list";
    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public List<ProductDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
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

    @PostMapping("/update")
    public ProductDto updatePost(@RequestBody ProductDto productDto) {

        return productService.update(productDto);
    }

    @GetMapping("/delete")
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
