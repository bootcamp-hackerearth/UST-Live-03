package com.ust.pos.product;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";

    private final ProductService productService;

    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("category", categoryService.findAllWithoutNull());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto productDto1 = productService.save(productDto);
        if (!productDto1.isSuccess()) {
            model.addAttribute("message", productDto1.getMessage());
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ProductDto productDto = productService.findByIdentifier(identifier);
        model.addAttribute("category", categoryService.findAllWithoutNull());
        model.addAttribute("product", productDto);
        return "product/product";
    }

    @PutMapping("/update")
    public String doupdate(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto productDto1 = productService.update(productDto);
        if (!productDto1.isSuccess()) {
            model.addAttribute("message", productDto1.getMessage());
            model.addAttribute("product", productDto);
            return "product/update";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @DeleteMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}
