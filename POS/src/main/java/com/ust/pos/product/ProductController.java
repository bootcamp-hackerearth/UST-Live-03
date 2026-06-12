package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelService;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.unit.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";
    public static final String CATEGORIES = "categories";
    public static final String PRODUCTS = "products";

    public static final String BRAND = "brand";
    public static final String UNIT = "unit";
    public static final String MODEL = "model";


    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private BrandService brandService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private ModelService modelService;


    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute(PRODUCTS, productService.findAll(pageable));
        return "product/list";
    }


    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(PRODUCTS, new ProductDto());
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        model.addAttribute(BRAND, brandService.findIfTrue());
        model.addAttribute(UNIT, unitService.findIfTrue());
        model.addAttribute(MODEL, modelService.findIfTrue());
        return "product/add";
    }


    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {

        ProductDto response = productService.save(productDto);

        if (!response.isSuccess()) {
            model.addAttribute(PRODUCTS, productDto);
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute("message", response.getMessage());
            return "product/add";
        }

        return REDIRECT_PRODUCT_LIST;
    }


    @GetMapping("/get")
    public String get(Model model, @RequestParam String identifier) {
        ProductDto product = productService.findByIdentifier(identifier);
        model.addAttribute(CATEGORIES, categoryService.findChildCategories());
        model.addAttribute(BRAND, brandService.findIfTrue());
        model.addAttribute(UNIT, unitService.findIfTrue());
        model.addAttribute(MODEL, modelService.findIfTrue());
        model.addAttribute(PRODUCTS, product);
        return "product/edit";
    }


    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {

        ProductDto response = productService.update(productDto);

        if (!response.isSuccess()) {
            model.addAttribute(PRODUCTS, productDto);
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute("message", response.getMessage());
            return "product/edit";
        }

        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

    @PostMapping("/toggle")
    public String toggleProduct(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
        return REDIRECT_PRODUCT_LIST;
    }


}