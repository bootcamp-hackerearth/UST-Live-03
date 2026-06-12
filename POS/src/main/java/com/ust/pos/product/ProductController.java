package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelsService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.shelf.service.ShelfService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/product")
@Controller
public class ProductController {

    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    public static final String PRODUCTS = "products";
    public static final String BRAND = "brand";
    public static final String MODEL = "model";
    public static final String CATEGORIES = "categories";
    public static final String UNIT = "unit";

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ShelfService shelfService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ModelsService modelsService;
    @Autowired
    private UnitService unitService;


    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        model.addAttribute(BRAND, brandService.findActiveBrands());
        model.addAttribute(MODEL, modelsService.findActiveModels());
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        model.addAttribute(UNIT, unitService.findActiveUnits());

        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto, Pageable pageable) {
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        model.addAttribute(BRAND, brandService.findActiveBrands());
        model.addAttribute(MODEL, modelsService.findActiveModels());
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        model.addAttribute(UNIT, unitService.findActiveUnits());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto, Pageable pageable) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(PRODUCTS, productService.findAll(pageable));
            model.addAttribute(BRAND, brandService.findActiveBrands());
            model.addAttribute(MODEL, modelsService.findActiveModels());
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute(UNIT, unitService.findActiveUnits());
            return "product/add";

        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        model.addAttribute("productDto", response);
        model.addAttribute(BRAND, brandService.findActiveBrands());
        model.addAttribute(MODEL, modelsService.findActiveModels());
        model.addAttribute(UNIT, unitService.findActiveUnits());
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute(BRAND, brandService.findActiveBrands());
            model.addAttribute(MODEL, modelsService.findActiveModels());
            model.addAttribute(UNIT, unitService.findActiveUnits());
            return "product/product";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}
