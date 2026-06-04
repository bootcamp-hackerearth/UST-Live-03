package com.ust.pos.product;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.models.service.ModelsService;
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

    private static final String BRAND = "brand";

    private static final String MODEL = "model";

    private static final String CATEGORIES = "categories";

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private ModelsService modelsService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("products", productService.findAll(pageable));
        return "product/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute ProductDto productDto) {
        model.addAttribute(CATEGORIES, categoryService.findBySuperCategoryNotNull());
        model.addAttribute(BRAND, brandService.findIfTrue());
        model.addAttribute("unit", unitService.findIfTrue());
        model.addAttribute(MODEL, modelsService.findIfTrue());
        return "product/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.save(productDto);
        if (!response.isSuccess()) {
            model.addAttribute(CATEGORIES, categoryService.findBySuperCategoryNotNull());
            model.addAttribute(BRAND, brandService.findIfTrue());
            model.addAttribute("unit", unitService.findIfTrue());
            model.addAttribute(MODEL, modelsService.findIfTrue());
            model.addAttribute("message", response.getMessage());
            return "product/add";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/get/{identifier}")
    public String update(Model model, @PathVariable String identifier) {
        ProductDto response = productService.findByIdentifier(identifier);
        model.addAttribute(CATEGORIES, categoryService.findBySuperCategoryNotNull());
        model.addAttribute("product", response);
        model.addAttribute(BRAND, brandService.findIfTrue());
        model.addAttribute("unit", unitService.findIfTrue());
        model.addAttribute(MODEL, modelsService.findIfTrue());
        return "product/product";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute ProductDto productDto) {
        ProductDto response = productService.update(productDto);
        if (!response.isSuccess()) {
            model.addAttribute("product", response);
            model.addAttribute(CATEGORIES, categoryService.findBySuperCategoryNotNull());
            model.addAttribute("message", response.getMessage());
            model.addAttribute(BRAND, brandService.findIfTrue());
            model.addAttribute("unit", unitService.findIfTrue());
            model.addAttribute(MODEL, modelsService.findIfTrue());
            return "product/product";
        }
        return REDIRECT_PRODUCT_LIST;
    }

    @GetMapping("/delete/{identifier}")
    public String delete(@PathVariable String identifier) {
        productService.delete(identifier);
        return REDIRECT_PRODUCT_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(@RequestParam String identifier) {
        productService.toggleStatus(identifier);
    }
}