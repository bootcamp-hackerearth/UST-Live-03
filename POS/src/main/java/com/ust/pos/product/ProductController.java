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

    public static final String MODELS = "models";
    public static final String BRANDS = "brands";
    public static final String UNITS = "units";
    public static final String CATEGORIES = "categories";
    public static final String PRODUCT_DTO = "productDto";
    public static final String MESSAGE = "message";
    private static final String REDIRECT = "redirect:/product/list";

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ModelsService modelService;
    private final UnitService unitService;

    public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService, ModelsService modelService, UnitService unitService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.modelService = modelService;
        this.unitService = unitService;
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(PRODUCT_DTO, new ProductDto());
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        model.addAttribute(BRANDS, brandService.findActiveBrands());
        model.addAttribute(MODELS, modelService.findActiveModels());
        model.addAttribute(UNITS, unitService.findActiveUnits());
        return "product/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute ProductDto productDto, Model model) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute(BRANDS, brandService.findActiveBrands());
            model.addAttribute(MODELS, modelService.findActiveModels());
            model.addAttribute(UNITS, unitService.findActiveUnits());
            model.addAttribute(PRODUCT_DTO, productDto);
            return "product/add";
        }
        return REDIRECT;
    }

    @GetMapping("/get")
    public String edit(@RequestParam String identifier, Model model, Pageable pageable) {
        ProductDto response = productService.findByIdentifier(identifier);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute("products", productService.findAll(pageable));
            return "product/list";
        }
        model.addAttribute(PRODUCT_DTO, response);
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        model.addAttribute(BRANDS, brandService.findActiveBrands());
        model.addAttribute(MODELS, modelService.findActiveModels());
        model.addAttribute(UNITS, unitService.findActiveUnits());
        return "product/product";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute ProductDto productDto, Model model) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute(BRANDS, brandService.findActiveBrands());
            model.addAttribute(MODELS, modelService.findActiveModels());
            model.addAttribute(UNITS, unitService.findActiveUnits());
            model.addAttribute(PRODUCT_DTO, productDto);
            return "product/product";
        }
        return REDIRECT;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
        return REDIRECT;
    }
}