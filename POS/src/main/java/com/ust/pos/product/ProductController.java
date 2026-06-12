package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelService;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {
    public static final String PRODUCT = "product";
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;
    @Autowired
    ModelService modelService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }


    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {
        model.addAttribute("category", categoryService.findSubCategories());
        model.addAttribute("brand", brandService.findActiveStatus());
        model.addAttribute("models", modelService.findActiveStatus());
        return "product/add";
    }

    @PostMapping("/add")
    public String addproduct(Model model, @ModelAttribute ProductDto productDto) {
        productService.save(productDto);
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute(PRODUCT, response);
        model.addAttribute("category", categoryService.findSubCategories());
        model.addAttribute("brand", brandService.findActiveStatus());
        model.addAttribute("models", modelService.findActiveStatus());
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        model.addAttribute(PRODUCT, response);
        if (!response.isSuccess()) {
            model.addAttribute(PRODUCT, response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        productService.changeToggleStatus(identifier, status);
        return REDIRECT_PRODUCT_LIST;
    }
}
