package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.service.ModelService;
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

    public static final String UNITS = "units";
    public static final String BRANDS = "brands";
    public static final String MODELS = "models";
    public static final String CATEGORIES = "categories";
    public static final String REDIRECT_PRODUCT_LIST = "redirect:/product/list";

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private UnitService unitService;

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {

        model.addAttribute("product", new ProductDto());
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        model.addAttribute(MODELS, modelService.findAll(pageable));
        model.addAttribute(UNITS, unitService.findAll(pageable));

        return "product/add";
    }

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/get")
    public String update(Model model, Pageable pageable, @RequestParam String identifier) {
        model.addAttribute("product", productService.findByIdentifier(identifier));
        model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
        model.addAttribute(BRANDS, brandService.findAll(pageable));
        model.addAttribute(MODELS, modelService.findAll(pageable));
        model.addAttribute(UNITS, unitService.findAll(pageable));

        return "product/product";
    }

    @PostMapping("/add")
    public String addPost(Model model, Pageable pageable, @ModelAttribute("product") ProductDto productDto) {
        ProductDto response = productService.save(productDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(CATEGORIES, categoryService.findChildCategories());
            model.addAttribute(BRANDS, brandService.findAll(pageable));
            model.addAttribute(MODELS, modelService.findAll(pageable));
            model.addAttribute(UNITS, unitService.findAll(pageable));

            return "product/add";
        }

        return REDIRECT_PRODUCT_LIST;
    }

    @PostMapping("/update")
    public String updatePost(Model model, Pageable pageable, @ModelAttribute("product") ProductDto productDto) {
        ProductDto response = productService.update(productDto);

        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            model.addAttribute(CATEGORIES, categoryService.findAll(pageable));
            model.addAttribute(BRANDS, brandService.findAll(pageable));
            model.addAttribute(MODELS, modelService.findAll(pageable));
            model.addAttribute(UNITS, unitService.findAll(pageable));

            return "product/product";
        }

        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
        return REDIRECT_PRODUCT_LIST;
    }
}