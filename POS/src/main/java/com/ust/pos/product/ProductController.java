package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelsService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ModelsService modelsService;
    private final BrandService brandService;
    private final UnitService unitService;

    public ProductController(ProductService productService, CategoryService categoryService, ModelsService modelsService, BrandService brandService, UnitService unitService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.modelsService = modelsService;
        this.brandService = brandService;
        this.unitService = unitService;
    }

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, Pageable pageable, @ModelAttribute ProductDto productDto) {
        model.addAttribute("categories", categoryService.findAllCategoriesWithNoSuper());
        model.addAttribute("models", modelsService.findAll(pageable));
        model.addAttribute("brands", brandService.findAll(pageable));
        model.addAttribute("units", unitService.findAll(pageable));
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "product/add";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute("categories", categoryService.findAllCategoriesWithNoSuper());
        model.addAttribute("product", response);
        model.addAttribute("models", modelsService.findAll(pageable));
        model.addAttribute("brands", brandService.findAll(pageable));
        model.addAttribute("units", unitService.findAll(pageable));
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());

        }
        model.addAttribute("products", productService.findAll(pageable));
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}