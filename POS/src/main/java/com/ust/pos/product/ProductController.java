package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {
    public static final String PRODUCT_DTO = "productDto";
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    @Autowired
    BrandService brandService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    public String findAll(Model model, Pageable pageable) {
        model.addAttribute(PRODUCT_DTO, productService.findAll(pageable));
        model.addAttribute("brand", brandService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/get")
    public String getsave(Model model, Pageable pageable) {
        model.addAttribute(PRODUCT_DTO, new ProductDto());
        model.addAttribute("categories", categoryService.findSubCategories());
        model.addAttribute("brand", brandService.findAll(pageable));
        return "product/add";
    }

    @PostMapping("/add")
    public String save(Model model, @ModelAttribute(PRODUCT_DTO) ProductDto productDto) {
        ProductDto response = productService.save(productDto);
        model.addAttribute("success", response.isStatus());
        model.addAttribute("message", response.getMessage());
        model.addAttribute(PRODUCT_DTO, new ProductDto());
        return "product/add";
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam Long id) {
        productService.delete(id);
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/update")
    public String update(Model model, @RequestParam Long id, Pageable pageable) {
        ProductDto response = productService.findById(id);
        model.addAttribute(PRODUCT_DTO, response);
        model.addAttribute("categories", categoryService.findSubCategories());
        model.addAttribute("brands", brandService.findAll(pageable));
        return "product/product";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        model.addAttribute(PRODUCT_DTO, response);
        return REDIRECT_PRODUCT_LIST;

    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        productService.changeProductStatus(identifier, status);
        return REDIRECT_PRODUCT_LIST;
    }
}